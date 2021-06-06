package com.imovil.recordapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

public class SelectUser extends AppCompatActivity implements RepositoryObserver {
    private static final String TAG = "SelectUser";
    public static final String ARG_USERS = "users";

    Repository repository;
    UsersListAdapter usersListAdapter;

    JsonElement jsonElement;

    private RecyclerView recyclerView;
    private Users users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_trial);

        repository = new Repository(this);

        Intent intent = getIntent();

        users = (Users) intent.getSerializableExtra(ARG_USERS);

        usersListAdapter = new UsersListAdapter();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(usersListAdapter);

        usersListAdapter.setUsers(users);
        usersListAdapter.setOnItemClickListener(new UsersListAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d(TAG, "onItemClick, pos: "+position);
                v.setBackgroundColor(Color.BLUE);
                String userID = users.getUsers().get(position).getUserID();
                repository.downloadTrialsListFromUserID(userID);
            }
        });
    }

    public void launchSelectTrial() {
        Gson gson =  new Gson();
        List<Trial> trials_info_list = gson.fromJson(this.jsonElement, new TypeToken<List<Trial>>() {}.getType());
        Trials trials_info = new Trials(trials_info_list);
        Log.d(TAG, String.valueOf(trials_info));

        Intent intent = new Intent(SelectUser.this, SelectTrial.class);

        intent.putExtra(SelectTrial.ARG_TRIALS, trials_info);
        intent.putExtra(SelectTrial.ARG_USER_TRIAL, true);
        startActivity(intent);
    }

    @Override
    public void onJsonDownloaded(JsonElement jsonElement, int jsonCode) {
        if (jsonCode==RepositoryObserver.USER_TRIALS_INFO) {
            this.jsonElement = jsonElement;
            launchSelectTrial();
        }
    }
}