package com.imovil.recordapp;

import com.google.gson.JsonElement;

public interface RepositoryObserver {

    void onJsonDownloaded(JsonElement jsonElement);

}
