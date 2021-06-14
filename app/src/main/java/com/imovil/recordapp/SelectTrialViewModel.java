package com.imovil.recordapp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class SelectTrialViewModel extends AndroidViewModel {

    Repository repository;
    private LiveData<Trials> userTrials, newTrials;
    private boolean isUserTrial;
    private String userID;

    public SelectTrialViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application.getApplicationContext());
    }

    public void init() {
        if(isUserTrial) {
            updateUserTrials();
            userTrials = repository.getUserTrials();
        }
        else {
            updateNewTrials();
            newTrials = repository.getNewTrials();
        }
    }

    public LiveData<Trials> getUserTrials() {
        return userTrials;
    }

    public LiveData<Trials> getNewTrials() {
        return newTrials;
    }

    public boolean isUserTrial() {
        return isUserTrial;
    }

    public void setUserTrial(boolean userTrial) {
        isUserTrial = userTrial;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void updateUserTrials() {
        repository.updateUserTrials(userID);
    }
    public void updateNewTrials() {
        repository.updateNewTrials();
    }

    public void updateTrials() {
        if (isUserTrial) {
            updateUserTrials();
        }
        else {
            updateNewTrials();
        }
    }
}
