package com.imovil.recordapp;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HostFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "ExplorerMenu";

    Activity activity;
    Button newTrialsButton, userTrialsButton;
    boolean isUserTrial = false;

    public HostFragment() {
        // Required empty public constructor
    }

    public static HostFragment newInstance(String param1, String param2) {
        HostFragment fragment = new HostFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mode_selection, container, false);

        activity = getActivity();

        newTrialsButton = v.findViewById(R.id.newTrialsButton);
        userTrialsButton = v.findViewById(R.id.userTrialsButton);
        newTrialsButton.setOnClickListener(this);
        userTrialsButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.newTrialsButton:
                //repository.downloadTrialsList();
                //repository.downloadUsers();
                isUserTrial = false;
                break;
            case R.id.userTrialsButton:
                //repository.downloadUsers();
                isUserTrial = true;
                break;
        }

        ((NavigationInterface)activity).onModeSelected(isUserTrial);
    }



}
