package com.imovil.NEURmetrics;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SelectTrialFragment extends Fragment {
    private static final String TAG = "SelectTrial";

    public static final String ARG_IS_USER_TRIAL = "isUserTrial";
    public static final String ARG_USER_ID = "userID";

    private Activity activity;
    private SharedSelectionViewModel model;

    // Base type for using inheritance to go userTrial or newTrial
    private RecyclerView.Adapter trialsListAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    public static SelectUserFragment newInstance(boolean isUserTrial, String userID) {
        SelectUserFragment fragment = new SelectUserFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_USER_TRIAL, isUserTrial);
        args.putString(ARG_USER_ID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(SharedSelectionViewModel.class);
        if (getArguments()!=null) {
            model.setUserTrial(getArguments().getBoolean(ARG_IS_USER_TRIAL));
            model.setUserID(getArguments().getString(ARG_USER_ID));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (model.isUserTrial()) { // Update user trials list
            model.getUserTrials().observe(requireActivity(), trials -> {
                ((UserTrialsListAdapter)trialsListAdapter).setTrials(trials);
                swipeRefreshLayout.setRefreshing(false);
            });
        }
        else { // Update new trials list
            model.getNewTrials().observe(requireActivity(), trials -> {
                ((NewTrialsListAdapter)trialsListAdapter).setTrials(trials);
                swipeRefreshLayout.setRefreshing(false);
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_select_trial, container, false);

        model.initSelectTrial();

        activity = getActivity();

        // Select between UsersTrialListAdapter y NewTrialListAdapter and set onItemClickListener
        if (model.isUserTrial()) {
            trialsListAdapter = new UserTrialsListAdapter();
            ((UserTrialsListAdapter)trialsListAdapter).setOnItemClickListener(this::onTrialClicked);
        }
        else {
            trialsListAdapter = new NewTrialsListAdapter();
            ((NewTrialsListAdapter)trialsListAdapter).setOnItemClickListener(this::onTrialClicked);
        }

        swipeRefreshLayout = view.findViewById(R.id.swipetorefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                model.updateTrials();
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(trialsListAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        return view;
    }

    private void onTrialClicked(int position, View v) {
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

        if (model.isUserTrial()) model.updateUserTrialInfo(position);
        else model.updateNewTrialInfo(position);

        ((NavigationInterface)activity).onTrialSelected();
    }
}
