package com.imovil.NEURmetrics;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class SharedSelectionViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<Users> users;
    private LiveData<Trials> userTrials, newTrials;
    private TrialInfo userTrialInfo;
    private TrialInfo newTrialInfo;
    private boolean isUserTrial;
    private String userID;
    private LiveData<Boolean> isTrialDownloaded;

    public SharedSelectionViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application.getApplicationContext());
        isTrialDownloaded = repository.isTrialDownloaded();
    }

    public void initialize_device() {
        repository.initialize_device();
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

    public void downloadTrial() {
        if(isUserTrial) {
            repository.downloadUserTrial(userTrialInfo.getUserID(), userTrialInfo.getStartTime());
        }
        else {
            repository.downloadNewTrial(newTrialInfo.getTrialID());
        }
    }

    public LiveData<Boolean> getIsTrialDownloaded() {
        return isTrialDownloaded;
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

    public void updateUserTrialInfo(int position) {
        userTrialInfo = userTrials.getValue().getTrials().get(position).getTrialInfo();
    }

    public void updateNewTrialInfo(int position) {
        newTrialInfo = newTrials.getValue().getTrials().get(position).getTrialInfo();

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

    public LiveData<Trials> getUserTrials() {
        return userTrials;
    }

    public LiveData<Trials> getNewTrials() {
        return newTrials;
    }

    public void updateTrials() {
        if (isUserTrial) {
            updateUserTrials();
        }
        else {
            updateNewTrials();
        }
    }

    public boolean isServerReachable() {
        return repository.isReachable();
    }

    public void onChangePreferences() {
        repository.updateBaseURL();
    }

    public Trial getTrial() {
        return repository.getTrial();
    }

    public void setIsTrialDownloaded(boolean isTrialDownloaded) {
        repository.setIsTrialDownloaded(isTrialDownloaded);
    }

    public void uploadNewUser(User user) {
        repository.uploadNewUser(user);
    }
}
