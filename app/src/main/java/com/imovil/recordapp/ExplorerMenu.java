package com.imovil.recordapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ExplorerMenu extends AppCompatActivity implements View.OnClickListener, RepositoryObserver {

    private static String TAG = "ExplorerMenu";

    Repository repository;
    Button getTrialsButton, getUsersButton, settingsButton;
    JsonElement jsonElementTrials, jsonElementUsers;
    boolean isNewTrial = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_explorer_menu);

        repository = new Repository(this);

        getTrialsButton = findViewById(R.id.getTrialsButton);
        getUsersButton = findViewById(R.id.getUsersButton);

        getTrialsButton.setOnClickListener(this);
        getUsersButton.setOnClickListener(this);

        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.getTrialsButton:
                //repository.downloadTrialsList();
                repository.downloadUsers();
                isNewTrial = true;
                break;
            case R.id.getUsersButton:
                repository.downloadUsers();
                isNewTrial = false;
                break;
            case R.id.settingsButton:
                Intent intent = new Intent(ExplorerMenu.this, Settings.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onJsonDownloaded(JsonElement jsonElement, int jsonCode) {
        switch (jsonCode) {
            /*case RepositoryObserver.TRIALS_INFO:
                this.jsonElementTrials = jsonElement;
                launchSelectTrial();
                break;*/

            case RepositoryObserver.USERS_LIST:
                this.jsonElementUsers = jsonElement;
                launchSelectUser(isNewTrial);
                break;

            case RepositoryObserver.ERROR:
                Toast.makeText(getApplicationContext(), "Error: Revisar conexion", Toast.LENGTH_SHORT).show();
                break;

        }
        Log.d(TAG, String.valueOf(jsonElement));
    }

    private void launchSelectUser(boolean isNewTrial) {
        Gson gson =  new Gson();
        List<User> users_list = gson.fromJson(this.jsonElementUsers, new TypeToken<List<User>>() {}.getType());
        Users users = new Users(users_list);
        Log.d(TAG, String.valueOf(users));

        Intent intent = new Intent(ExplorerMenu.this, SelectUser.class);

        intent.putExtra(SelectUser.ARG_USERS, users);
        intent.putExtra(SelectUser.ARG_IS_NEW_TRIAL, isNewTrial);
        startActivity(intent);
    }
}
