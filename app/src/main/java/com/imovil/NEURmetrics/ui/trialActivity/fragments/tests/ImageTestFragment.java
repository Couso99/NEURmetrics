package com.imovil.NEURmetrics.ui.trialActivity.fragments.tests;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import android.widget.TextView;

import com.imovil.NEURmetrics.R;
import com.imovil.NEURmetrics.models.Test;
import com.imovil.NEURmetrics.models.TrialInfo;
import com.imovil.NEURmetrics.ui.trialActivity.TrialActivity;
import com.imovil.NEURmetrics.viewmodels.TrialViewModel;

import java.io.File;
import java.io.FileOutputStream;

public class ImageTestFragment extends Fragment implements View.OnClickListener {
    Activity activity;

    TrialViewModel model;

    private Test test;
    private TrialInfo trialInfo;
    private Button recordButton, nextButton;
    private ImageView imageView;
    private TextView textView;
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
        textView = view.findViewById(R.id.testText);
        nextButton = view.findViewById(R.id.finishedButton);

        activity = getActivity();

        model.getIsRecording().observe(requireActivity(), this::onIsRecordingChanged);

        trialInfo = model.getTrial().getTrialInfo();
        test = model.getTest();

        int parametersSize = test.getParametersNumber();

        String fname=null, text=null;

        for (int i=0; i<parametersSize; i++) {
            switch (test.getParametersType().get(i)) {
                case "filename":
                    fname = test.getParameters().get(i);
                    break;
                case "text":
                    text = test.getParameters().get(i);
                    break;
            }
        }

        switch (test.getTestType()) {
            case TrialActivity.TEST_RECORD_OVER_IMAGE:
                textView.setVisibility(View.GONE);
                loadImage(fname);
                break;

            case TrialActivity.TEST_RECORD_OVER_TEXT:
                imageView.setVisibility(View.GONE);
                loadText(text);
                break;
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
                else model.startRecording(fileName);
                break;
            case R.id.finishedButton:
                model.uploadFile(fileName, "audio/*");
                model.nextTest();
                break;
        }
    }

    private void loadImage(String fname) {
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

    private void loadText(String text) {
        textView.setText(text);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Bitmap image = Bitmap.createBitmap(textView.getMeasuredWidth(),
                        textView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(image);

                canvas.drawColor(Color.WHITE);
                textView.draw(canvas);

                String textImageFname = test.getName() + "_text_parameters_screenshot.jpeg";

                FileOutputStream fileOutputStream;
                try {
                    fileOutputStream = new FileOutputStream(new File(model.getFilePath(textImageFname)));
                    image.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        thread.start();
    }

    // Callback for updating start/stop recording button
    public void onIsRecordingChanged(boolean isRecording) {
        if (isRecording) {
            recordButton.setBackgroundColor(Color.RED);
            test.setOutputFilename(outputFilename);
            recordButton.setText(R.string.isRecordingButton);
            this.isRecording = true;
        }
        else {
            recordButton.setBackgroundColor(Color.rgb(30,30,30));
            recordButton.setText(R.string.startRecordingButton);
            this.isRecording = false;
        }
    }
}