package com.imovil.recordapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;

import com.google.gson.JsonElement;

public class HostActivityViewModel extends ViewModel {

    private MutableLiveData<JsonElement> jsonElementUsers = new MutableLiveData<>();
    private boolean isNewTrial;
    private MutableLiveData<JsonElement> jsonElementTrials = new MutableLiveData<>();
    private String userID;
    private MutableLiveData<JsonElement> jsonElementTrial = new MutableLiveData<>();

    public LiveData<JsonElement> getJsonElementUsers() {
        return jsonElementUsers;
    }

    public void setJsonElementUsers(MutableLiveData<JsonElement> jsonElementUsers) {
        this.jsonElementUsers = jsonElementUsers;
    }

    public boolean isNewTrial() {
        return isNewTrial;
    }

    public void setNewTrial(boolean newTrial) {
        isNewTrial = newTrial;
    }

    public LiveData<JsonElement> getJsonElementTrials() {
        return jsonElementTrials;
    }

    public void setJsonElementTrials(MutableLiveData<JsonElement> jsonElementTrials) {
        this.jsonElementTrials = jsonElementTrials;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public LiveData<JsonElement> getJsonElementTrial() {
        return jsonElementTrial;
    }

    public void setJsonElementTrial(MutableLiveData<JsonElement> jsonElementTrial) {
        this.jsonElementTrial = jsonElementTrial;
    }

    public void onJsonDownloaded(JsonElement jsonElement, int jsonCode) {
        switch (jsonCode) {
            case RepositoryObserver.USER_TRIALS_INFO:
            case RepositoryObserver.TRIALS_INFO:
                this.jsonElementTrials.setValue(jsonElement);
                break;

            case RepositoryObserver.USERS_LIST:
                this.jsonElementUsers.setValue(jsonElement);
                break;

            case RepositoryObserver.ERROR:
                break;
            case RepositoryObserver.TRIAL:
                this.jsonElementTrial.setValue(jsonElement);
                break;
        }
    }


}
