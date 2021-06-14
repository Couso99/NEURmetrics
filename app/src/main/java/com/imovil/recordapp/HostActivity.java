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

    HostActivityViewModel model;

    ActivityResultLauncher<String> askPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_host);

        model = new ViewModelProvider(this).get(HostActivityViewModel.class);

        model.getJsonElementTrial().observe(this, jsonElement -> launchTrial(jsonElement, model.isNewTrial(), model.getUserID()));
        model.getJsonElementTrials().observe(this, jsonElement -> launchSelectTrial(jsonElement, model.isNewTrial(), model.getUserID()));
        model.getJsonElementUsers().observe(this, jsonElement -> launchSelectUser(jsonElement, model.isNewTrial()));

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

    private void launchSelectUser(JsonElement jsonElement, boolean isNewTrial) {
        Gson gson =  new Gson();
        List<User> users_list = gson.fromJson(jsonElement, new TypeToken<List<User>>() {}.getType());
        Users users = new Users(users_list);
        Log.d(TAG, String.valueOf(users));

        Bundle bundle = new Bundle();

        bundle.putSerializable(SelectUserFragment.ARG_USERS, users);
        bundle.putSerializable(SelectUserFragment.ARG_IS_NEW_TRIAL, isNewTrial);

        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_hostFragment_to_selectUserFragment, bundle);
    }

    public void launchSelectTrial(JsonElement jsonElement, boolean isNewTrial, String userID) {
        Gson gson =  new Gson();
        List<Trial> trials_info_list = gson.fromJson(jsonElement, new TypeToken<List<Trial>>() {}.getType());
        Trials trials_info = new Trials(trials_info_list);
        Log.d(TAG, String.valueOf(trials_info));

        Bundle bundle = new Bundle();

        bundle.putSerializable(SelectTrialFragment.ARG_TRIALS, trials_info);
        bundle.putSerializable(SelectTrialFragment.ARG_IS_USER_TRIAL, !isNewTrial);
        bundle.putSerializable(SelectTrialFragment.ARG_USER_ID, isNewTrial ? userID:null);

        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_selectUserFragment_to_selectTrialFragment, bundle);
    }

    public void launchTrial(JsonElement jsonElement, boolean isNewTrial, String userID) {
        Gson gson = new Gson();
        List<Trial> trial_list = gson.fromJson(jsonElement, new TypeToken<List<Trial>>() {}.getType());
        if (trial_list.size()>0)
        {
            Trial trial = trial_list.get(0);
            if(isNewTrial) trial.getTrialInfo().setUserID(userID);
            Log.d(TAG, String.valueOf(trial));

            Intent intent = new Intent(HostActivity.this, TestActivity.class);

            intent.putExtra(TestActivity.ARG_TRIAL, trial);

            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(),R.string.error_no_trial_found,Toast.LENGTH_SHORT).show();
        }
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
    public void isNewTrial(boolean isNewTrial) {
        model.setNewTrial(isNewTrial);
    }

    @Override
    public void onUserSelected(String userID) {
        model.setUserID(userID);
    }


    @Override
    public void onJsonDownloaded(JsonElement jsonElement, int jsonCode) {
        model.onJsonDownloaded(jsonElement, jsonCode);
    }
}