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
    private static final String ARG_TESTS = "tests";

    Activity activity;
    TestsListAdapter testsListAdapter;

    private RecyclerView recyclerView;
    private Button nextButton;
    private TextView trialView;

    private Trial trial;

    public ResultsFragment() {
        // Required empty public constructor
    }

    public static ResultsFragment newInstance(Trial trial) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TESTS, trial);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trial = (Trial) getArguments().getSerializable(ARG_TESTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_results, container, false);

        activity = getActivity();

        testsListAdapter = new TestsListAdapter();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(testsListAdapter);

        testsListAdapter.setTests(trial);

        nextButton = view.findViewById(R.id.finishedButton);
        nextButton.setOnClickListener(v -> ((TrialInterface) activity).endTrial());

        TrialInfo trialInfo = trial.getTrialInfo();

        trialView = view.findViewById(R.id.trialScoreTextView);
        trialView.setText("Total score:\t"+ trialInfo.getTotalScore() + " /"+trialInfo.getTotalMaxScore());

        return view;
    }
}