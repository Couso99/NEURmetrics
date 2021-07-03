package com.imovil.NEURmetrics;

import android.app.Application;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class TrialViewModel extends AndroidViewModel {
    private Repository repository;

    private Trial trial;
    private Test test;

    private int testIndex, testSubIndex;
    private long recorderBaseTime;

    private boolean isUserTrial, isScoreDuringTests = true, isTestScored = true;
    private boolean isShowingResults;

    private MutableLiveData<Boolean> isUpdateHeaders = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLaunchScoring = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLaunchNextTest = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLaunchTrialResults = new MutableLiveData<>();
    private LiveData<Boolean> isRecording;
    private LiveData<Boolean> isDataUploaded;

    private final RecorderPlayer recorder = new RecorderPlayer();

    public TrialViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application.getApplicationContext());
        isDataUploaded = repository.isDataUploaded();
        isRecording = recorder.isRecording();
    }

    public Trial getTrial() {
        return trial;
    }

    public void setTrial(Trial trial) {
        this.trial = trial;

        isScoreDuringTests = !trial.getTrialInfo().isTrialScored();
        //navigateTests(0,0);
    }

    public boolean isUserTrial() {
        return isUserTrial;
    }

    public void setUserTrial(boolean userTrial) {
        isUserTrial = userTrial;
    }

    public boolean isScoreDuringTests() {
        return isScoreDuringTests;
    }

    public void setScoreDuringTests(boolean scoreDuringTests) {
        isScoreDuringTests = scoreDuringTests;
    }

    public boolean isTestScored() {
        return isTestScored;
    }

    public void setTestScored(boolean testScored) {
        isTestScored = testScored;
    }

    public LiveData<Boolean> getIsUpdateHeaders() {
        return isUpdateHeaders;
    }

    public void setIsUpdateHeaders(boolean isUpdateHeaders) {
        this.isUpdateHeaders.setValue(isUpdateHeaders);
    }

    public LiveData<Boolean> getIsRecording() {
        return isRecording;
    }

    public LiveData<Boolean> getIsDataUploaded() {
        return isDataUploaded;
    }

    public LiveData<Boolean> getIsLaunchScoring() {
        return isLaunchScoring;
    }

    public void setIsLaunchScoring(boolean isLaunchScoring) {
        this.isLaunchScoring.setValue(isLaunchScoring);
    }

    public boolean isShowingResults() {
        return isShowingResults;
    }

    public LiveData<Boolean> getIsLaunchNextTest() {
        return isLaunchNextTest;
    }

    public void setIsLaunchNextTest(boolean isLaunchNextTest) {
        this.isLaunchNextTest.setValue(isLaunchNextTest);
    }

    public LiveData<Boolean> getIsLaunchTrialResults() {
        return isLaunchTrialResults;
    }

    public void setIsLaunchTrialResults(boolean isLaunchTrialResults) {
        this.isLaunchTrialResults.setValue(isLaunchTrialResults);
        if (isLaunchTrialResults) {
            isShowingResults=true;
            setIsUpdateHeaders(true);
        }
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
        if (!isUserTrial) this.test.setStartTestTimeOffset(TrialTimer.getElapsedTime());
        setIsUpdateHeaders(true);
        isTestScored = false;
    }

    public void initTrial() {

        isUserTrial = trial.getTrialInfo().isTrialScored();

        if (!isUserTrial) {
            TrialTimer.init_timer();
            trial.getTrialInfo().setStartTime(TrialTimer.getStartTime());
        }

        startDownloadingTests(trial);

    }

    public void updateTrialInfo() {
        int totalScore=0, totalMaxScore=0;
        int testScore, testMaxScore;
        List<Test> test_p;

        for (Test test : trial.getTests()) {
            if (test.isContainsTests()){
                test_p = test.getTests();
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

        trial.getTrialInfo().setTotalMaxScore(totalMaxScore);
        if (isScoreDuringTests){
            trial.getTrialInfo().setTotalScore(totalScore);
            trial.getTrialInfo().setTrialScored(true);
        }
    }

    public void postTrial() {
        //updateTrialInfo();
        if(isUserTrial) {repository.updateUserTrial(trial);}
        else {repository.uploadUserTrial(trial);}
    }

    private void navigateTests(int index, int subindex) {
        Test test = trial.getTests().get(index);
        testIndex = index;

        if (test.isContainsTests()) {
            if (subindex==-2)
                subindex = test.getTests().size()-1;

            setTest(test.getTests().get(subindex));
            testSubIndex = subindex;
            return;
        }
        setTest(test);
        testSubIndex = -1;
    }

    public int nextTest() {

        stopPlaying();

        if (test==null) {
            navigateTests(0,0);
            if (isUserTrial) {
                setIsLaunchScoring(true);
                isTestScored = true;
            }
            else setIsLaunchNextTest(true);
            return 0;
        }

        if (isTestScored || !isScoreDuringTests) {
            if (testSubIndex == -1) {
                if (testIndex >= trial.getTests().size() - 1) {
                    updateTrialInfo();
                    setIsLaunchTrialResults(true);
                    return -1;
                }
                navigateTests(testIndex + 1, 0);
            } else {
                if (testSubIndex >= trial.getTests().get(testIndex).getTests().size() - 1) {
                    if (testIndex >= trial.getTests().size() - 1) {
                        updateTrialInfo();
                        setIsLaunchTrialResults(true);
                        return -1;
                    }
                    navigateTests(testIndex + 1, 0);
                } else {
                    navigateTests(testIndex, testSubIndex + 1);
                }
            }

            if (!isUserTrial) setIsLaunchNextTest(true);
            else setIsLaunchScoring(true);
        }
        else {
            setIsLaunchScoring(true);
            if (isScoreDuringTests) test.setStopTestTimeOffset(TrialTimer.getElapsedTime());
            isTestScored = true;
        }
        return 0;

    }

    public boolean previousTest() {
        if (isShowingResults) {
            isShowingResults = false;
            return true;
        }
        if (testIndex==0 && testSubIndex<=0) return false;
        if (isScoreDuringTests && isTestScored) {
            isTestScored=false;
            return true;
        }

        if (testSubIndex>0) {
            navigateTests(testIndex, testSubIndex-1);
            if (isScoreDuringTests) isTestScored=true;
        }
        else {
            navigateTests(testIndex-1, -2);
            if (isScoreDuringTests) isTestScored=true;
        }
        return true;
    }

    private void startDownloadingTests(Trial trial) {
        boolean isTrialScored = trial.getTrialInfo().isTrialScored();

        for (Test test : trial.getTests()) {
            if (test.getParametersNumber() != 0) {
                List<String> parameters_type = test.getParametersType();
                List<String> parameters = test.getParameters();

                for (int i=0; i<test.getParametersNumber(); i++)
                    if (parameters_type.get(i).equals("filename"))
                        repository.downloadFile(parameters.get(0));
            }
            if (isTrialScored)
                if (test.getOutputFilename() != null)
                    repository.downloadUserMadeFile(test.getOutputFilename());
            if (test.isContainsTests())
                for (Test child_test: test.getTests()) {
                    if (child_test.getParametersNumber() != 0) {
                        List<String> parameters_type = child_test.getParametersType();
                        List<String> parameters = child_test.getParameters();

                        for (int i=0; i<child_test.getParametersNumber(); i++)
                            if (parameters_type.get(i).equals("filename"))
                                repository.downloadFile(parameters.get(0));
                    }

                    if (isTrialScored)
                        if (child_test.getOutputFilename() != null)
                            repository.downloadUserMadeFile(child_test.getOutputFilename());
                }
        }
    }

    public void uploadFile(String fileName, String mediaType) {
        repository.uploadGeneral(fileName, mediaType);
    }

    public String getFilePath(String fileName) {
        return repository.getFilePath(fileName);
    }

    public void startRecording(String fileName, int recording_time_ms) {
        recorder.startRecording(fileName, recording_time_ms);
        recorderBaseTime = SystemClock.elapsedRealtime();
    }

    public void stopRecording() {
        recorder.stopRecording();
        recorderBaseTime = 0;
    }

    public void startPlaying(String fileName) {
        recorder.startPlaying(fileName);
    }

    public void stopPlaying() {
        recorder.stopPlaying();
    }

    public long getRecorderBaseTime() {
        return recorderBaseTime;
    }
}
