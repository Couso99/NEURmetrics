package com.imovil.recordapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Fragment;

import java.util.List;

public class TestActivity extends AppCompatActivity implements TrialInterface {
    //todo implement timing of tests

    private final static String TAG = "TestActivity";
    private final String outputJsonFname = "nombre_aqui.json";

    Repository repository;

    Test test, test_piece;
    List<Test> tests_list, test_pieces_list;
    Tests tests;
    TrialInfo trialInfo;
    int test_index=0, isLastTest = 0, isTestScored=0;
    int test_pieces_index=0;
    boolean isScoreDuringTests=true;
    boolean isTrialScored = false;
    boolean isRunTestPiece = false;

    private RecorderPlayer recorder;
    private RecorderObserver observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        recorder = new RecorderPlayer();
        repository = new Repository(this);

        Bundle b = getIntent().getExtras();
        tests = (Tests) b.getSerializable("tests");
        tests_list = tests.getTests();

        trialInfo = tests.getTrialInfo();
        if (!(isTrialScored = trialInfo.isTrialScored()))
            isTrialScored = false;

        startDownloadingTests(tests);
        nextTest();
    }

    @Override
    protected void onStart() {
        super.onStart();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private int updateTest() {
        if (isRunTestPiece) {
            if (test_pieces_index < test_pieces_list.size()) {
                test_piece = test_pieces_list.get(test_pieces_index);
                test_pieces_index++;
                return 0;
            }
        }
        isRunTestPiece = false;

        if (test_index < tests_list.size()) {
            test = tests_list.get(test_index);
            test_index++;
            test_pieces_list = test.getTestPieces();
            if (test_pieces_list!= null) {
                isRunTestPiece = true;
                test_pieces_index = 0;
                return updateTest();
            }
            return 0;
        }
        return -1;
    }

    private void startPlaying(String fileName) {
        recorder.startPlaying(fileName);
    }

    private void stopPlaying() {
        recorder.stopPlaying();
    }

    public void startRecording(String fileName, int recording_time_ms) {
        recorder.startRecording(fileName, recording_time_ms);
        observer.onIsRecordingChanged(1);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(200);
                        if (!recorder.getIsRecording()) {
                            TestActivity.this.runOnUiThread(() -> observer.onIsRecordingChanged(0));
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    public void stopRecording() {
        recorder.stopRecording();
        observer.onIsRecordingChanged(0);
    }

    public void uploadFile(String fileName, String mediaType) {
        repository.uploadGeneral(fileName, mediaType);
    }

    private void prepareJson(Tests tests) {
        int totalScore=0, totalMaxScore=0;
        int testScore, testMaxScore;
        List<Test> test_p;

        for (Test test : tests.getTests()) {
            if ((test_p = test.getTestPieces())!= null){
                testScore = 0;
                testMaxScore=0;

                for (Test test_piece : test_p){
                    testScore += test_piece.getScore();
                    testMaxScore += test_piece.getMaxScore();
                }
                test.setScore(testScore);
                test.setMaxScore(testMaxScore);
            }

            totalScore += test.getScore();
            totalMaxScore += test.getMaxScore();
        }

        trialInfo.setTotalScore(totalScore);
        trialInfo.setTotalMaxScore(totalMaxScore);
        if (isScoreDuringTests)
                trialInfo.setTrialScored(true);
    }

    private void startDownloadingTests(Tests tests) {
        boolean isTrialScored = tests.getTrialInfo().isTrialScored();

        for (Test test : tests.getTests()) {
            if (test.getParametersNumber() != 0) repository.downloadImage(test.getParameters().get(0));
            if (isTrialScored && test.getOutputFilename() != null)
                repository.downloadUserMadeFile(test.getOutputFilename());
        }
    }

    private Fragment nextTestNewFragment(Test t) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("test", t);

        //String testID = t.getTestID();
        //int testType = Integer.parseInt(String.valueOf(testID.charAt(0)));

        Fragment test_fragment;

        switch (t.getTestType()) {//testType) {
            case 0:
                test_fragment = new ImageTestFragment();
                observer = (RecorderObserver) test_fragment;
                break;
            case 1:
                test_fragment = new DrawingFragment();
                break;
            case 2:
                test_fragment = new TapLettersFragment();
                break;
            default:
                test_fragment = new ImageTestFragment();
                break;
        }

        test_fragment.setArguments(bundle);

        return test_fragment;
    }

    @Override
    public String getFilePath(String fname) {
        return repository.getFilePath(fname);
    }

    @Override
    public void nextTest() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (test!=null && isScoreDuringTests && isTestScored==0) {
            scoreTest();
            isTestScored = 1;
        }
        else if (!isTrialScored){
            stopPlaying();

            isLastTest = updateTest();

            if (isLastTest == 0) {
                Fragment test_fragment = nextTestNewFragment(isRunTestPiece ? test_piece : test);

                fragmentTransaction.replace(R.id.relativeLayout, test_fragment);
                fragmentTransaction.commit();

                if (isScoreDuringTests) {
                    isTestScored = 0;
                }
            }
        }
        else {
            scoreTest();
        }
        if (isLastTest != 0){
            String fname = outputJsonFname;
            prepareJson(tests);
            repository.writeJsonToDisk(tests, fname);
            repository.uploadJson(repository.getFilePath(fname));
            if (isScoreDuringTests || isTrialScored) testsResult();
        }
    }

    @Override
    public void scoreTest() {
        if(isTrialScored) {
            stopPlaying();
            isLastTest = updateTest();
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putSerializable("test", isRunTestPiece ? test_piece : test);
        bundle.putBoolean("isTrialScored", isTrialScored);

        Fragment test_fragment = new ScoringFragment();
        test_fragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.relativeLayout, test_fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void testsResult() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putSerializable("tests", tests);

        Fragment test_fragment = new ResultsFragment();
        test_fragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.relativeLayout, test_fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void endTrial() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (repository.isOutputJsonUploaded()) {
                            finish();
                            break;
                        }
                        sleep(200);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }
}
