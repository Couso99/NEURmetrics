package com.imovil.NEURmetrics;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class SelectionActivity extends AppCompatActivity implements NavigationInterface, DatePickerDialog.OnDateSetListener {
    AppBarConfiguration appBarConfiguration;

    private static String TAG = "ExplorerMenu";

    SharedSelectionViewModel model;
    NewUserViewModel newUserModel;

    ActivityResultLauncher<String> askPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_selection);

        model = new ViewModelProvider(this).get(SharedSelectionViewModel.class);
        newUserModel = new ViewModelProvider(this).get(NewUserViewModel.class);

        askPermission.launch(Manifest.permission.RECORD_AUDIO);
        askPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        askPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        model.getIsTrialDownloaded().observe(this,isTrialDownloaded -> {
            if (isTrialDownloaded) {
                model.setIsTrialDownloaded(false);
                launchTrial();
            }
        });

        newUserModel.getIsUploadUser().observe(this, isUploadUser -> {
            onUserCreated(newUserModel.getUser());
        });

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void launchCreateUser() {
        try {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_selectUserFragment_to_createUserFragment);
        } catch (java.lang.IllegalArgumentException ignored) {}
    }

    private void launchSelectUser() {
        try {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_homeScreenFragment_to_selectUserFragment);
        } catch (java.lang.IllegalArgumentException ignored) {}
    }

    public void launchSelectTrial() {
        try {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_selectUserFragment_to_selectTrialFragment);
        } catch (java.lang.IllegalArgumentException ignored) {}
    }

    @Override
    public void launchSettings() {
        try {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_homeScreenFragment_to_settingsFragment);
        } catch (java.lang.IllegalArgumentException ignored) {}
    }

    @Override
    public void onModeSelected(boolean isUserTrial) {
        model.setUserTrial(isUserTrial);

        Thread thread = new Thread() {
            @Override
            public void run() {
                if (model.isServerReachable())
                    runOnUiThread(()->launchSelectUser());
                else
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(),"No hay conexión con el servidor",Toast.LENGTH_SHORT).show());
            }
        };

        thread.start();
    }

    @Override
    public void onUserSelected(String userID) {
        model.setUserID(userID);
        launchSelectTrial();
        Log.d("WebService", "onUserSelected: "+model.getUserID());
    }

    public void onUserCreated(User user) {
        model.uploadNewUser(user);
        onBackPressed();
        // todo fetch new user ID, then invoke onUserSelected and launch select trial directly
    }

    @Override
    public void onTrialSelected() {
        model.downloadTrial();
    }

    @Override
    public void launchTrial() {
        Trial trial = model.getTrial();
        trial.getTrialInfo().setUserID(model.getUserID());

        Intent intent = new Intent(SelectionActivity.this, TrialActivity.class);
        intent.putExtra(TrialActivity.ARG_TRIAL, trial);
        startActivity(intent);
    }

    @Override
    public void onCreateUser() {
        launchCreateUser();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        newUserModel.setBirthday(year,month,dayOfMonth);
    }
}