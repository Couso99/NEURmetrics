package com.imovil.recordapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class SelectTrialFragment extends Fragment {
    private static final String TAG = "SelectTrial";

    public static final String ARG_TRIALS = "trials_info";
    public static final String ARG_IS_USER_TRIAL = "isUserTrial";
    public static final String ARG_USER_ID = "userID";

    Activity activity;
    Repository repository;

    RecyclerView.Adapter trialsListAdapter;
    JsonElement jsonElement;

    String userID;

    private RecyclerView recyclerView;
    private Trials trials;
    private boolean isUserTrial;

    public static SelectUserFragment newInstance(Trials trials, boolean isUserTrial, String userID) {
        SelectUserFragment fragment = new SelectUserFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIALS, trials);
        args.putBoolean(ARG_IS_USER_TRIAL, isUserTrial);
        args.putString(ARG_USER_ID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trials = (Trials) getArguments().getSerializable(ARG_TRIALS);
            isUserTrial = (boolean) getArguments().getBoolean(ARG_IS_USER_TRIAL);
            userID = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.activity_select_trial, container, false);

        activity = getActivity();
        repository = new Repository(activity);

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
                                activity.runOnUiThread(() -> v.setBackgroundColor(Color.WHITE));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    thread.start();

                    TrialInfo trialInfo = trials.getTrials().get(position).getTrialInfo();
                    String userID = trialInfo.getUserID();
                    long startTime = trialInfo.getStartTime();
                    //Toast.makeText(getApplicationContext(),"UserTrialsAdapter",Toast.LENGTH_SHORT);
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
                                activity.runOnUiThread(() -> v.setBackgroundColor(Color.WHITE));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    thread.start();

                    TrialInfo trialInfo = trials.getTrials().get(position).getTrialInfo();
                    String trialID = trialInfo.getTrialID();
                    //Toast.makeText(getApplicationContext(),"NewTrialsAdapter",Toast.LENGTH_SHORT);
                    repository.downloadTrialFromTrialID(trialID);
                }
            });
        }


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(trialsListAdapter);

        return view;
    }
}
