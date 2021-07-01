package com.imovil.recordapp;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class ImageTestFragment extends Fragment implements View.OnClickListener {
    Activity activity;

    TrialViewModel model;

    private Test test;
    private TrialInfo trialInfo;
    private Button recordButton, nextButton;
    private ImageView imageView;
    private int recording_time_ms = 0;

    private static String fileName = null, outputFilename;
    private boolean isRecording = false;


    public ImageTestFragment() {
        // Required empty public constructor
    }

    public static ImageTestFragment newInstance(Test test, TrialInfo trialInfo) {
        ImageTestFragment fragment = new ImageTestFragment();
        Bundle args = new Bundle();
        args.putSerializable(TrialActivity.ARG_TEST, test);
        args.putSerializable(TrialActivity.ARG_TRIAL_INFO, trialInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(TrialViewModel.class);

        if (getArguments() != null) {
            trialInfo = (TrialInfo) getArguments().getSerializable(TrialActivity.ARG_TRIAL_INFO);
            test = (Test) getArguments().getSerializable(TrialActivity.ARG_TEST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_test, container, false);

        recordButton = view.findViewById(R.id.recordButton);
        imageView = view.findViewById(R.id.imageView);
        nextButton = view.findViewById(R.id.finishedButton);

        activity = getActivity();

        model.getIsRecording().observe(requireActivity(), this::onIsRecordingChanged);

        trialInfo = model.getTrial().getTrialInfo();
        test = model.getTest();
        if (test.getParameters().get(0) != null) {
            String fname = test.getParameters().get(0);
            File file = new File(model.getFilePath(fname));
            if (file.exists()) {
                imageView.setImageURI(Uri.fromFile(file));
                imageView.setAdjustViewBounds(true);
            }
            else {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            while(true) {
                                sleep(200);
                                if (file.exists()) {
                                    activity.runOnUiThread(() -> {
                                        imageView.setImageURI(Uri.fromFile(file));
                                        imageView.setAdjustViewBounds(true);});
                                    break;
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

                thread.start();
            }
        }

        outputFilename = test.getName()+'_'+trialInfo.getUserID()+"_"+trialInfo.getStartTime()+"_audio.3gp";
        fileName = model.getFilePath(outputFilename);

        recordButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.recordButton:
                if (isRecording) model.stopRecording();
                else model.startRecording(fileName, recording_time_ms);
                break;
            case R.id.finishedButton:
                model.uploadFile(fileName, "audio/*");
                model.nextTest();
                break;
        }
    }

    public void onIsRecordingChanged(boolean isRecording) {
        if (isRecording) {
            recordButton.setBackgroundColor(Color.RED);
            test.setOutputFilename(outputFilename);
            recordButton.setText("Grabando");
            this.isRecording = true;
        }
        else {
            recordButton.setBackgroundColor(Color.rgb(30,30,30));
            recordButton.setText("Pulse para grabar");
            this.isRecording = false;
        }
    }
}