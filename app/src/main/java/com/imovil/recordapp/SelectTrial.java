package com.imovil.recordapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class SelectTrial extends AppCompatActivity implements RepositoryObserver{
    private static final String TAG = "SelectTrial";

    public static final String ARG_TRIALS = "trials_info";
    public static final String ARG_IS_USER_TRIAL = "isUserTrial";
    public static final String ARG_USER_ID = "userID";

    Repository repository;

    RecyclerView.Adapter trialsListAdapter;
    JsonElement jsonElement;

    String userID;

    private RecyclerView recyclerView;
    private Trials trials;
    private boolean isUserTrial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_select_trial);

        repository = new Repository(this);

        Intent intent = getIntent();

        trials = (Trials) intent.getSerializableExtra(ARG_TRIALS);
        isUserTrial = intent.getBooleanExtra(ARG_IS_USER_TRIAL,false);
        userID = intent.getStringExtra(ARG_USER_ID);

        if (isUserTrial) {
            trialsListAdapter = new UserTrialsListAdapter();
            ((UserTrialsListAdapter)trialsListAdapter).setTrials(trials);
            ((UserTrialsListAdapter)trialsListAdapter).setOnItemClickListener(new UserTrialsListAdapter.ClickListener() {
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

                    TrialInfo trialInfo = trials.getTrials().get(position).getTrialInfo();
                    String userID = trialInfo.getUserID();
                    long startTime = trialInfo.getStartTime();
                    Toast.makeText(getApplicationContext(),"UserTrialsAdapter",Toast.LENGTH_SHORT);
                    repository.downloadUserTrial(userID,startTime);
                }
            });
        }

        else {
            trialsListAdapter = new NewTrialsListAdapter();
            ((NewTrialsListAdapter)trialsListAdapter).setTrials(trials);
            ((NewTrialsListAdapter)trialsListAdapter).setOnItemClickListener(new NewTrialsListAdapter.ClickListener() {
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

                    TrialInfo trialInfo = trials.getTrials().get(position).getTrialInfo();
                    String trialID = trialInfo.getTrialID();
                    Toast.makeText(getApplicationContext(),"NewTrialsAdapter",Toast.LENGTH_SHORT);
                    repository.downloadTrialFromTrialID(trialID);
                }
            });
        }


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(trialsListAdapter);

    }

    @Override
    public void onJsonDownloaded(JsonElement jsonElement, int jsonCode) {
        Log.d(TAG, String.valueOf(jsonCode));
        if (jsonCode==RepositoryObserver.TRIAL) {
            this.jsonElement = jsonElement;
            launchTrial();
        }
    }

    private void launchTrial() {
        Gson gson = new Gson();
        List<Trial> trial_list = gson.fromJson(this.jsonElement, new TypeToken<List<Trial>>() {}.getType());
        if (trial_list.size()>0)
        {
            Trial trial = trial_list.get(0);
            trial.getTrialInfo().setUserID(userID);
            Log.d(TAG, String.valueOf(trial));

            Intent intent = new Intent(SelectTrial.this, TestActivity.class);

            intent.putExtra(TestActivity.ARG_TRIAL, trial);
            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(),R.string.error_no_trial_found,Toast.LENGTH_SHORT).show();
        }
    }
}