package com.imovil.recordapp;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class HostActivity extends AppCompatActivity implements RepositoryObserver, HostFragment.HostInterface, SelectUserFragment.UserInterface, HomeScreenFragment.HomeScreenInterface {
    AppBarConfiguration appBarConfiguration;

    private static String TAG = "ExplorerMenu";

    SharedSelectionViewModel model;

    ActivityResultLauncher<String> askPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_host);

        model = new ViewModelProvider(this).get(SharedSelectionViewModel.class);

        askPermission.launch(Manifest.permission.RECORD_AUDIO);
        askPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        askPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void launchSelectUser() {
        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_hostFragment_to_selectUserFragment);
    }

    public void launchSelectTrial(boolean isUserTrial, String userID) {
        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_selectUserFragment_to_selectTrialFragment);
    }

    @Override
    public void launchSettings() {

        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_homeScreenFragment_to_settingsFragment);

    }

    @Override
    public void enterSearchMode() {
        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_homeScreenFragment_to_hostFragment);

    }

    @Override
    public void isUserTrial(boolean isUserTrial) {
        model.setUserTrial(isUserTrial);
        launchSelectUser();
    }

    @Override
    public void onUserSelected(String userID) {
        model.setUserID(userID);
        launchSelectTrial(model.isUserTrial(),model.getUserID());
        Log.d("WebService", "onUserSelected: "+model.getUserID());
    }

    @Override
    public void onJsonDownloaded(JsonElement jsonElement, int jsonCode) {
     //   model.onJsonDownloaded(jsonElement, jsonCode);
    }
}