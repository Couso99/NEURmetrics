package com.imovil.recordapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HostFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static String TAG = "ExplorerMenu";

    View v;

    Activity activity;
    Repository repository;
    Button getTrialsButton, getUsersButton, settingsButton;
    JsonElement jsonElementTrials, jsonElementUsers;
    boolean isNewTrial = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HostFragment() {
        // Required empty public constructor
    }

    public interface HostInterface {
        void isNewTrial(boolean isNewTrial);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HostFragment newInstance(String param1, String param2) {
        HostFragment fragment = new HostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_explorer_menu, container, false);
        Button button1, button2;


        activity = getActivity();

        repository = new Repository(activity);

        button1 = (Button) v.findViewById(R.id.newTrialsButton);
        button2 = (Button) v.findViewById(R.id.userTrialsButton);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        return v;
    }



    @Override
    public void onClick(View v) {
        this.v = v;
        int id = v.getId();

        switch (id) {
            case R.id.newTrialsButton:
                //repository.downloadTrialsList();
                repository.downloadUsers();
                isNewTrial = true;
                break;
            case R.id.userTrialsButton:
                repository.downloadUsers();
                isNewTrial = false;
                break;
        }

        ((HostInterface)activity).isNewTrial(isNewTrial);
    }



}
