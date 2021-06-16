package com.imovil.recordapp;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class ImageTestFragment extends Fragment implements View.OnClickListener, RecorderObserver{
    Activity activity;

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
        //if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        //}
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

        if (getArguments() != null) {
            trialInfo = (TrialInfo) getArguments().getSerializable(TrialActivity.ARG_TRIAL_INFO);
            test = (Test) getArguments().getSerializable(TrialActivity.ARG_TEST);
            if (test.getParameters().get(0) != null) {
                String fname = test.getParameters().get(0);
                File file = new File(((TrialInterface)activity).getFilePath(fname));
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
        }


        outputFilename = test.getName()+'_'+trialInfo.getUserID()+"_"+trialInfo.getStartTime()+"_audio.3gp";
        fileName = ((TrialInterface)activity).getFilePath(outputFilename);

        recordButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.recordButton:
                if (isRecording) ((TrialInterface) activity).stopRecording();
                else ((TrialInterface) activity).startRecording(fileName, recording_time_ms);
                break;
            case R.id.finishedButton:
                ((TrialInterface) activity).uploadFile(fileName, "audio/*");
                ((TrialInterface) activity).nextTest();
                break;
        }
    }

    @Override
    public void onIsRecordingChanged(int isRecording) {
        if (isRecording==0) {
            recordButton.setBackgroundColor(0x303030);
            recordButton.setText("Pulse para grabar de nuevo");
            this.isRecording = false;
        }
        else {
            recordButton.setBackgroundColor(Color.RED);
            test.setOutputFilename(outputFilename);
            recordButton.setText("Grabando");
            this.isRecording = true;
        }
    }
}