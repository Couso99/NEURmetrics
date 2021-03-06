package com.imovil.NEURmetrics.ui.trialActivity.fragments.tests;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.imovil.NEURmetrics.R;
import com.imovil.NEURmetrics.models.Test;
import com.imovil.NEURmetrics.models.TrialInfo;
import com.imovil.NEURmetrics.ui.trialActivity.views.DrawingView;
import com.imovil.NEURmetrics.ui.trialActivity.TrialActivity;
import com.imovil.NEURmetrics.viewmodels.TrialViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DrawingFragment extends Fragment {
    Activity activity;

    TrialViewModel model;

    private Test test;
    private TrialInfo trialInfo;
    private DrawingView drawingView;
    private Button finishedButton;
    private TextView commentTextView;

    private static String fileName = null, outputFilename;

    public DrawingFragment() {
        // Required empty public constructor
    }

    public static DrawingFragment newInstance(Test test, TrialInfo trialInfo) {
        DrawingFragment fragment = new DrawingFragment();
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

        /*if (getArguments() != null) {
            test = (Test) getArguments().getSerializable(TrialActivity.ARG_TEST);
            trialInfo = (TrialInfo) getArguments().getSerializable(TrialActivity.ARG_TRIAL_INFO);
        }*/

        test = model.getTest();
        trialInfo = model.getTrial().getTrialInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drawing, container, false);

        activity = getActivity();

        //init_headers();

        //commentTextView = view.findViewById(R.id.commentTextView);
        drawingView = (DrawingView) view.findViewById(R.id.drawing_area);
        drawingView.initTrailDrawer();
        //drawingArea.setImageURI(Uri.fromFile(new File(getContext().getExternalCacheDir()+ File.separator+"fondo_app2.jpg")));

        if (test.getParametersNumber() != 0) {
            String fname = test.getParameters().get(0);
                File file = new File(model.getFilePath(fname));
                if (file.exists()) {
                    drawingView.setImageURI(Uri.fromFile(file));
                    drawingView.setAdjustViewBounds(true);
                } else {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                while (true) {
                                    sleep(200);
                                    if (file.exists()) {
                                        activity.runOnUiThread(() ->
                                        {
                                            drawingView.setImageURI(Uri.fromFile(file));
                                            drawingView.setAdjustViewBounds(true);
                                        });
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

        outputFilename = test.getName() +'_'+trialInfo.getUserID()+"_"+trialInfo.getStartTime()+ "_draw.jpeg";
        fileName = model.getFilePath(outputFilename);

        finishedButton = view.findViewById(R.id.finishedButton);
        finishedButton.setOnClickListener(v -> {
            Bitmap image = screenShot(drawingView);
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream(new File(fileName));
                image.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            model.uploadFile(fileName, "image/*");

            List<String> outputs = new ArrayList<>();
            List<String> outputsType = new ArrayList<>();
            outputs.add(outputFilename);
            outputsType.add("filename");

            test.setOutputs(outputs);
            test.setOutputsType(outputsType);
            test.setOutputsNumber(outputs.size());
            model.nextTest();
        });

        return view;
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}