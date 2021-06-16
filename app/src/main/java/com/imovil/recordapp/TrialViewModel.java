package com.imovil.recordapp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class TrialViewModel extends AndroidViewModel {
    Repository repository;

    Trial trial;
    Test test;

    boolean isUserTrial;

    private MutableLiveData<Boolean> isUpdateHeaders = new MutableLiveData<>();


    public TrialViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application.getApplicationContext());

    }

    public Trial getTrial() {
        return trial;
    }

    public void setTrial(Trial trial) {
        this.trial = trial;
    }

    public boolean isUserTrial() {
        return isUserTrial;
    }

    public void setUserTrial(boolean userTrial) {
        isUserTrial = userTrial;
    }

    public LiveData<Boolean> getIsUpdateHeaders() {
        return isUpdateHeaders;
    }

    public void setIsUpdateHeaders(boolean isUpdateHeaders) {
        this.isUpdateHeaders.setValue(isUpdateHeaders);
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
        setIsUpdateHeaders(true);
        Log.d("VIEWMODELOS","CAMBIOVALORVARIABLE");
    }

    public void initTrial() {

        isUserTrial = trial.getTrialInfo().isTrialScored();

        if (isUserTrial) {
            TrialTimer.init_timer();
            trial.getTrialInfo().setStartTime(TrialTimer.getStartTime());
        }

        startDownloadingTests(trial);
    }

    private void updateTrialInfo() {
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

        trial.getTrialInfo().setTotalScore(totalScore);
        trial.getTrialInfo().setTotalMaxScore(totalMaxScore);
        if (isUserTrial)
            trial.getTrialInfo().setTrialScored(true);
    }

    public void endTrial() {
        updateTrialInfo();
        if(isUserTrial) {repository.updateUserTrial(trial);}
        else {repository.uploadUserTrial(trial);}
        //repository.writeJsonToDisk(trial, fname);
        //repository.uploadJson(repository.getFilePath(fname));
       
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
}
