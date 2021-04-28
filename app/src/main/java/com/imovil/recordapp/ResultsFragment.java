package com.imovil.recordapp;

import android.app.Activity;
import android.os.Bundle;

import android.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment {
    Activity activity;
    TestsListAdapter testsListAdapter;

    private RecyclerView recyclerView;
    private Button nextButton;
    private TextView trialView;

    private Tests tests;

    public ResultsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ResultsFragment newInstance(String param1, String param2) {
        ResultsFragment fragment = new ResultsFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_results, container, false);

        if (getArguments() != null) {
            tests = (Tests) getArguments().getSerializable("tests");
        }

        activity = getActivity();

        testsListAdapter = new TestsListAdapter();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(testsListAdapter);

        testsListAdapter.setTests(tests);

        nextButton = view.findViewById(R.id.finishedButton);
        nextButton.setOnClickListener(v -> ((ComunicaTest) activity).endTrial());

        TrialInfo trialInfo = tests.getTrialInfo();

        trialView = view.findViewById(R.id.trialScoreTextView);
        trialView.setText("Total score:\t"+ trialInfo.getTotalScore() + " /"+trialInfo.getTotalMaxScore());

        return view;
    }
}