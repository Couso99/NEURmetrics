package com.imovil.recordapp;

import com.google.gson.JsonElement;

public interface RepositoryObserver {

    int TRIAL = 0;
    int TRIALS_INFO = 1;
    int USERS_LIST = 2;
    int USER_TRIALS_INFO = 3;

    void onJsonDownloaded(JsonElement jsonElement, int jsonCode);

}
