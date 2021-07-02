package com.imovil.NEURmetrics;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class HomeScreenFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = "AUDIO_RECORDER";
    private final static String TAG = "WebService";
    //private final String jsonFname = "try.json";

    Activity activity;

    //WebService webService;
    SharedSelectionViewModel model;


    Button newTrialsButton, userTrialsButton;
    TextView isRecordingView;
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

        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        activity = getActivity();

        model = new ViewModelProvider(this).get(SharedSelectionViewModel.class);

        newTrialsButton = view.findViewById(R.id.newTrialsButton);
        userTrialsButton = view.findViewById(R.id.userTrialsButton);
        newTrialsButton.setOnClickListener(this);
        userTrialsButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.newTrialsButton:
                model.initialize_device();
                model.setUserTrial(false);
                break;
            case R.id.userTrialsButton:
                model.initialize_device();
                model.setUserTrial(true);
                break;
        }

        ((NavigationInterface)activity).onModeSelected(model.isUserTrial());
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
        //getActivity().getActionBar().setTitle("Nombre de la app");
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
                ((NavigationInterface)activity).launchSettings();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
