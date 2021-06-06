package com.imovil.recordapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class SelectOrCreateUser extends AppCompatActivity implements RepositoryObserver {
    private static final String TAG = "SelectUser";
    public static final String ARG_USERS = "users";

    Repository repository;
    UsersListAdapter usersListAdapter;

    FloatingActionButton mAddFab;

    JsonElement jsonElement;

    private RecyclerView recyclerView;
    private Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_or_create_user);

        repository = new Repository(this);

        users = (Users) getIntent().getSerializableExtra(ARG_USERS);
        usersListAdapter = new UsersListAdapter();

        mAddFab = findViewById(R.id.floatingActionButton);
        mAddFab.show();
        mAddFab.setOnClickListener(v -> addUser());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(usersListAdapter);

        usersListAdapter.setUsers(users);
        usersListAdapter.setOnItemClickListener(new UsersListAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d(TAG, "onItemClick, pos: " + position);
                v.setBackgroundColor(Color.BLUE);
                String userID = users.getUsers().get(position).getUserID();
                repository.downloadTrialsList();
            }
        });
    }

    private void addUser() {

    }


    public void launchSelectTrial() {
        Gson gson = new Gson();
        List<Trial> trials_info_list = gson.fromJson(this.jsonElement, new TypeToken<List<Trial>>() {
        }.getType());
        Trials trials_info = new Trials(trials_info_list);
        Log.d(TAG, String.valueOf(trials_info));

        Intent intent = new Intent(SelectOrCreateUser.this, SelectTrial.class);

        intent.putExtra(SelectTrial.ARG_TRIALS, trials_info);
        intent.putExtra(SelectTrial.ARG_USER_TRIAL, false);
        startActivity(intent);
    }

    @Override
    public void onJsonDownloaded(JsonElement jsonElement, int jsonCode) {
        if (jsonCode == RepositoryObserver.TRIALS_INFO) {
            this.jsonElement = jsonElement;
            launchSelectTrial();
        }
    }
}