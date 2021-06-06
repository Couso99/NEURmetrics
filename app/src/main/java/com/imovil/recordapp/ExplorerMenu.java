package com.imovil.recordapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

public class ExplorerMenu extends AppCompatActivity implements View.OnClickListener, RepositoryObserver {

    private static String TAG = "ExplorerMenu";

    Repository repository;
    Button getTrialsButton, getUsersButton, settingsButton;
    JsonElement jsonElementTrials, jsonElementUsers;
    boolean isDoTrial = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.getTrialsButton:
                //repository.downloadTrialsList();
                repository.downloadUsers();
                isDoTrial = true;
                break;
            case R.id.getUsersButton:
                repository.downloadUsers();
                isDoTrial = false;
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
            case RepositoryObserver.TRIALS_INFO:
                this.jsonElementTrials = jsonElement;
                launchSelectTrial();
                break;

            case RepositoryObserver.USERS_LIST:
                this.jsonElementUsers = jsonElement;
                if (isDoTrial) launchSelectTrial();
                else launchSelectUser();
                break;

        }
        Log.d(TAG, String.valueOf(jsonElement));
    }

    // todo juntar en la misma clase SelectUser y SelectOrCreateUser
    private void launchSelectTrial() {
        Gson gson =  new Gson();
        List<User> users_list = gson.fromJson(this.jsonElementUsers, new TypeToken<List<User>>() {}.getType());
        Users users = new Users(users_list);
        Log.d(TAG, String.valueOf(users));

        Intent intent = new Intent(ExplorerMenu.this, SelectOrCreateUser.class);

        intent.putExtra(SelectUser.ARG_USERS, users);
        startActivity(intent);
    }

    private void launchSelectUser() {
        Gson gson =  new Gson();
        List<User> users_list = gson.fromJson(this.jsonElementUsers, new TypeToken<List<User>>() {}.getType());
        Users users = new Users(users_list);
        Log.d(TAG, String.valueOf(users));

        Intent intent = new Intent(ExplorerMenu.this, SelectUser.class);

        intent.putExtra(SelectUser.ARG_USERS, users);
        startActivity(intent);

    }

}
