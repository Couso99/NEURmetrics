package com.imovil.recordapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.List;

public class HomeScreenFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = "AUDIO_RECORDER";
    private final static String TAG = "WebService";
    //private final String jsonFname = "try.json";

    private final String deviceID = "dev_1";

    Activity activity;

    //WebService webService;
    HomeScreenViewModel model;

    Button recordButton, stopButton;
    TextView isRecordingView, decibel;
    ImageView imageView;

    public static SelectUserFragment newInstance() {
        SelectUserFragment fragment = new SelectUserFragment();
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
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.activity_home_screen, container, false);

        activity = getActivity();

        model = new ViewModelProvider(this).get(HomeScreenViewModel.class);

        recordButton = view.findViewById(R.id.recordButton);
        stopButton = view.findViewById(R.id.stopButton);
        isRecordingView = view.findViewById(R.id.isRecordingView);
        decibel = view.findViewById(R.id.decibel);
        imageView = view.findViewById(R.id.imageView);

        recordButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);

        recordButton.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.stopButton:
                model.initialize_device(deviceID);
                enterSearchMode();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsButton:
                ((HomeScreenInterface)activity).launchSettings();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void enterSearchMode() {
        ((HomeScreenInterface)activity).enterSearchMode();
    }

    public interface HomeScreenInterface {
        void enterSearchMode();
        void launchSettings();
    }
}
