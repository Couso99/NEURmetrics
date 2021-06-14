package com.imovil.recordapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class SharedSelectionViewModel extends AndroidViewModel {
    Repository repository;
    private LiveData<Users> users;
    private LiveData<Trials> userTrials, newTrials;
    private boolean isUserTrial;
    private String userID;

    public SharedSelectionViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application.getApplicationContext());
    }

    public void initialize_device(String deviceID) {
        repository.initialize_device(deviceID);
    }

    public void initSelectUsers() {
        repository.updateUsersData();
        users = repository.getUsers();
    }

    public void initSelectTrial() {
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

    public void updateUserID(int position) {
        userID = users.getValue().getUsers().get(position).getUserID();
    }

    public void updateUsers() {
        repository.updateUsersData();
    }

    public LiveData<Users> getUsers() {
        return users;
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
