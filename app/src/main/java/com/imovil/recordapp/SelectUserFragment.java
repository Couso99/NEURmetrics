package com.imovil.recordapp;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SelectUserFragment extends Fragment {
    private static final String TAG = "SelectUser";

    //public static final String ARG_USERS = "users";
    public static final String ARG_IS_USER_TRIAL = "isUserTrial";

    private Activity activity;
    private SharedSelectionViewModel model;

    private UsersListAdapter usersListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton mAddFab;


    public static SelectUserFragment newInstance(boolean isUserTrial) {
        SelectUserFragment fragment = new SelectUserFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_USER_TRIAL, isUserTrial);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(SharedSelectionViewModel.class);

        if (getArguments() != null) {
            model.setUserTrial((boolean) getArguments().getBoolean(ARG_IS_USER_TRIAL));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model.getUsers().observe(requireActivity(), users -> {
            usersListAdapter.setUsers(users);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_select_user, container, false);

        model.initSelectUsers();

        activity = getActivity();

        mAddFab = view.findViewById(R.id.floatingActionButton);
        if (!model.isUserTrial()) {
            mAddFab.show();
            mAddFab.setOnClickListener(v -> addUser());
        }
        else mAddFab.hide();

        usersListAdapter = new UsersListAdapter();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(usersListAdapter);

        swipeRefreshLayout = view.findViewById(R.id.swipetorefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                model.updateUsers();
            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

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
                            activity.runOnUiThread(() -> v.setBackgroundColor(Color.WHITE));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

                thread.start();

                model.updateUserID(position);
                ((UserInterface)activity).onUserSelected(model.getUserID());
                //model.downloadTrialsList();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_user, menu);
        MenuItem search=menu.findItem(R.id.action_search);

        //this 2 lines
        SearchView searchView=(SearchView)search.getActionView();
        search(searchView);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_search:
                // search action
                SearchView searchView=(SearchView)item.getActionView();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void search(SearchView searchView) {
        if (searchView!=null) {

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    newText = newText.toLowerCase();
                    ArrayList<User> newList = new ArrayList<>();
                    for (User user : model.getUsers().getValue().getUsers()) {
                        String name = user.getName().toLowerCase();
                        String surname = user.getSurname().toLowerCase();
                        String centre = user.getCentre()!=null ? user.getCentre().toLowerCase() : "";
                        if (name.contains(newText) || surname.contains(newText) || centre.contains(newText)) {
                            newList.add(user);
                        }
                    }
                    usersListAdapter.setFilter(newList);
                    return true;
                }
            });
        }
    }

    private void addUser() {

    }

    public interface UserInterface {
        void onUserSelected(String userID);
    }
}