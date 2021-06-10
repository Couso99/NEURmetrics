package com.imovil.recordapp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class SelectUser extends AppCompatActivity implements RepositoryObserver {
    private static final String TAG = "SelectUser";
    public static final String ARG_USERS = "users";
    public static final String ARG_IS_NEW_TRIAL = "isNewTrial";

    Repository repository;
    UsersListAdapter usersListAdapter;

    JsonElement jsonElement;

    private RecyclerView recyclerView;
    private FloatingActionButton mAddFab;

    private Users users;

    private boolean isNewTrial;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().setWindowAnimations(R.style.SlideSelectUser);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_select_user);

        repository = new Repository(this);

        Intent intent = getIntent();

        isNewTrial = intent.getBooleanExtra(ARG_IS_NEW_TRIAL, false);
        users = (Users) intent.getSerializableExtra(ARG_USERS);


            mAddFab = findViewById(R.id.floatingActionButton);
        if (isNewTrial) {
            mAddFab.show();
            mAddFab.setOnClickListener(v -> addUser());
        }
        else mAddFab.hide();

        usersListAdapter = new UsersListAdapter();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(usersListAdapter);

        usersListAdapter.setUsers(users);
        usersListAdapter.setOnItemClickListener(new UsersListAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d(TAG, "onItemClick, pos: "+position);
                v.setBackgroundColor(getResources().getColor(R.color.colorItemSelected));
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            sleep(200);
                            runOnUiThread(() -> v.setBackgroundColor(Color.WHITE));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

                thread.start();

                userID = users.getUsers().get(position).getUserID();
                if (isNewTrial) repository.downloadTrialsList();
                else repository.downloadTrialsListFromUserID(userID);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void addUser() {

    }

    public void launchSelectTrial(boolean isNewTrial) {
        Gson gson =  new Gson();
        List<Trial> trials_info_list = gson.fromJson(this.jsonElement, new TypeToken<List<Trial>>() {}.getType());
        Trials trials_info = new Trials(trials_info_list);
        Log.d(TAG, String.valueOf(trials_info));

        Intent intent = new Intent(SelectUser.this, SelectTrial.class);

        intent.putExtra(SelectTrial.ARG_TRIALS, trials_info);
        intent.putExtra(SelectTrial.ARG_IS_USER_TRIAL, !isNewTrial);
        intent.putExtra(SelectTrial.ARG_USER_ID, userID);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onJsonDownloaded(JsonElement jsonElement, int jsonCode) {
        switch (jsonCode) {
            case RepositoryObserver.USER_TRIALS_INFO:
                //this.jsonElement = jsonElement;
                //launchSelectTrial(isNewTrial);
                //break;
            case RepositoryObserver.TRIALS_INFO:
                this.jsonElement = jsonElement;
                launchSelectTrial(isNewTrial);
                break;
        }
    }
}