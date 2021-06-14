package com.imovil.recordapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

public class HomeScreenViewModel extends AndroidViewModel {

    Repository repository;

    public HomeScreenViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application.getApplicationContext());
    }

    public void initialize_device(String deviceID) {
        repository.initialize_device(deviceID);
    }
}
