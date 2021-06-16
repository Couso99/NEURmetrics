package com.imovil.recordapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Fragment;

import java.util.List;

public class TrialActivity extends AppCompatActivity implements TrialInterface {
    private final static String TAG = "TestActivity";
    private final String outputJsonFname = "nombre_aqui.json";

    public final static String ARG_TRIAL = "trial";
    public static final String ARG_TEST = "test";
    public static final String ARG_TRIAL_INFO = "trialInfo";
    public static final String ARG_IS_USER_TRIAL = "isUserTrial";

    Repository repository;

    TrialViewModel model;

    Test test, test_piece;
    List<Test> tests_list, test_pieces_list;
    Trial trial;
    TrialInfo trialInfo;
    int test_index=0, isLastTest = 0, isTestScored=0;
    int test_pieces_index=0;
    boolean isScoreDuringTests=true;
    boolean isRunTestPiece = false;

    //todo mover recorder a ImageTestFragment y a cualquiera que lo utilice??
    private RecorderPlayer recorder;
    private RecorderObserver observer;

    //private TrialTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trial);

        recorder = new RecorderPlayer();

        repository = new Repository(this);

        model = new ViewModelProvider(this).get(TrialViewModel.class);

        //model = new ViewModelProvider(this).get(SharedSelectionViewModel.class);

        //model.getNewTrials().observe();

        Bundle b = getIntent().getExtras();

        model.setTrial((Trial) b.getSerializable(ARG_TRIAL));

        trial = model.getTrial();
        tests_list = trial.getTests();

        trialInfo = trial.getTrialInfo();

        model.initTrial();

        update_hdr();

        //startDownloadingTests(trial);
        nextTest();

        //NavController navController = Navigation.findNavController(this, R.id.headers_host_fragment);
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
       // });

    }

    @Override
    protected void onResume() {
        super.onResume();
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

            if (test.isContainsTests()) {
                test_pieces_list = test.getTests();
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
                        if (!recorder.isRecording()) {
                            TrialActivity.this.runOnUiThread(() -> observer.onIsRecordingChanged(0));
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

    private void prepareJson(Trial trial) {
        int totalScore=0, totalMaxScore=0;
        int testScore, testMaxScore;
        List<Test> test_p;

        for (Test test : trial.getTests()) {
            if ((test_p = test.getTests())!= null){
                testScore = 0;
                testMaxScore=0;

                for (Test test_piece : test_p){
                    testScore += test_piece.getScore();
                    if (test_piece.getMaxScore()>0) testMaxScore += test_piece.getMaxScore();
                }
                test.setScore(testScore);
                test.setMaxScore(testMaxScore);
            }

            totalScore += test.getScore();
            if (test.getMaxScore()>0) totalMaxScore += test.getMaxScore();
        }

        trialInfo.setTotalScore(totalScore);
        trialInfo.setTotalMaxScore(totalMaxScore);
        if (isScoreDuringTests)
                trialInfo.setTrialScored(true);
    }

    private Fragment nextTestNewFragment(Test t, TrialInfo trialInfo) {
        Fragment test_fragment;

        switch (t.getTestType()) {//testType) {
            case 1:
                test_fragment = DrawingFragment.newInstance(t, trialInfo);
                break;
            case 2:
                test_fragment = TapLettersFragment.newInstance(t, trialInfo);
                break;
            case 3:
                test_fragment = ImageTestFragment.newInstance(t, trialInfo);
                observer = (RecorderObserver) test_fragment;
                break;
            default:
                test_fragment = ImageTestFragment.newInstance(t, trialInfo);
                break;
        }

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
        else if (!model.isUserTrial()){
            stopPlaying();

            isLastTest = updateTest();

            if (isLastTest == 0) {
                Fragment test_fragment = nextTestNewFragment(isRunTestPiece ? test_piece : test, trialInfo);
                update_headers(isRunTestPiece ? test_piece : test);
                (isRunTestPiece ? test_piece : test).setStartTestTimeOffset(TrialTimer.getElapsedTime());
                model.setTest(isRunTestPiece ? test_piece : test);

                fragmentTransaction.replace(R.id.trial_host_fragment, test_fragment);
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
            //String fname = outputJsonFname;
            prepareJson(trial);


            if(model.isUserTrial()) {repository.updateUserTrial(trial);}
            else {repository.uploadUserTrial(trial);}
            //repository.writeJsonToDisk(trial, fname);
            //repository.uploadJson(repository.getFilePath(fname));
            if (isScoreDuringTests || model.isUserTrial()) testsResult();
        }
    }

    @Override
    public void scoreTest() {
        if(model.isUserTrial()) {
            stopPlaying();
            isLastTest = updateTest();
        } else {
            (isRunTestPiece ? test_piece : test).setStopTestTimeOffset(TrialTimer.getElapsedTime());
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment test_fragment = ScoringFragment.newInstance(isRunTestPiece ? test_piece : test, model.isUserTrial());
        update_headers(isRunTestPiece ? test_piece : test);

        fragmentTransaction.replace(R.id.trial_host_fragment, test_fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void testsResult() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment test_fragment = ResultsFragment.newInstance(trial);

        fragmentTransaction.replace(R.id.trial_host_fragment, test_fragment);
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

    public void update_headers(Test test) {
        /*FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment headers_fragment = HeadersFragment.newInstance(test.getTitle(), test.getH1(), test.getH2());

        fragmentTransaction.replace(R.id.headers_host_fragment, headers_fragment);
        fragmentTransaction.commit();*/

        /*getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.headers_host_fragment, HeadersFragment.class, null)
                .commit();*/
    }

    public void update_hdr() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.headers_host_fragment, HeadersFragment.class, null)
                .commit();
    }

}
