package com.imovil.recordapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class SelectUserViewModel extends AndroidViewModel {

    Repository repository;
    private LiveData<Users> users;
    private boolean isNewTrial;
    private String userID;

    public SelectUserViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application.getApplicationContext());
        repository.updateUsersData();
        users = repository.getUsers();
    }

    public boolean isNewTrial() {
        return isNewTrial;
    }

    public void setNewTrial(boolean newTrial) {
        isNewTrial = newTrial;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void downloadTrialsList() {

        if (isNewTrial) repository.downloadTrialsList();
        else repository.downloadTrialsListFromUserID(userID);
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
}
