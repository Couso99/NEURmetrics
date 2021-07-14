package com.imovil.NEURmetrics.ui.trialActivity.fragments.tests;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imovil.NEURmetrics.R;
import com.imovil.NEURmetrics.models.Test;
import com.imovil.NEURmetrics.models.TrialInfo;
import com.imovil.NEURmetrics.ui.trialActivity.TrialActivity;
import com.imovil.NEURmetrics.ui.trialActivity.views.TappableTextView;
import com.imovil.NEURmetrics.viewmodels.TrialViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CheckboxesFragment extends Fragment {

    Activity activity;

    TrialViewModel model;
    private Test test;
    private TrialInfo trialInfo;

    private Button finishedButton;

    private String fileName, outputFilename;
    private List<CheckBox> checkBoxes = new ArrayList<>();


    public CheckboxesFragment() {
        // Required empty public constructor
    }

    public static CheckboxesFragment newInstance(Test test) {
        CheckboxesFragment fragment = new CheckboxesFragment();
        Bundle args = new Bundle();
        args.putSerializable(TrialActivity.ARG_TEST, test);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(TrialViewModel.class);

        if (getArguments() != null) {
            test = (Test) getArguments().getSerializable(TrialActivity.ARG_TEST);
            if (test!=null) model.setTest(test);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checkboxes, container, false);

        activity = getActivity();

        trialInfo = model.getTrial().getTrialInfo();
        test = model.getTest();

        int parametersSize = test.getParametersNumber();

        LinkedList<String> texts = new LinkedList<>();

        for (int i=0; i<parametersSize; i++) {
            switch (test.getParametersType().get(i)) {
                case "text":
                    texts.add(test.getParameters().get(i));
                    break;
            }
        }

        LinearLayout linearLayoutVertical = view.findViewById(R.id.linearLayoutCheckboxes);


        for (int i=0;i<texts.size();i++) {
            LinearLayout linearLayout = new LinearLayout(activity);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER);

            TextView textView = new TextView(activity, null);
            textView.setText(texts.get(i));
            textView.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setPadding(32,16,32,16);
            textView.setTextSize(48);
            textView.setTextColor(Color.BLACK);

            linearLayout.addView(textView);
            CheckBox checkBox = new CheckBox(activity);
            checkBoxes.add(checkBox);

            linearLayout.addView(checkBox);

            linearLayoutVertical.addView(linearLayout);
        }

        outputFilename = test.getName()+'_'+trialInfo.getUserID()+"_"+trialInfo.getStartTime()+ "_screenshot.jpeg";
        fileName = model.getFilePath(outputFilename);

        finishedButton = view.findViewById(R.id.finishedButton);
        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (texts.size()== test.getScoreOptions().size()) {
                    List<Integer> scoreWeights = test.getScoreWeights();
                    boolean isScoreWeights = false;
                    if (scoreWeights != null)
                        isScoreWeights = true;
                    int activeCheckBoxes=0;
                    List<Integer> expandedScore = new ArrayList<>();
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        if (checkBoxes.get(i).isChecked()) {
                            activeCheckBoxes+=(isScoreWeights ? scoreWeights.get(i) : 1);
                            expandedScore.add(isScoreWeights ? scoreWeights.get(i) : 1);
                        } else
                            expandedScore.add(0);
                    }

                    if (test.getMaxScore()!=0) test.setScore(activeCheckBoxes);
                    else test.setScore(0);
                }

                Bitmap imagen = screenShot(linearLayoutVertical);
                FileOutputStream fileOutputStream;
                try {
                    fileOutputStream = new FileOutputStream(new File(fileName));
                    imagen.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
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
            }
        });

        return view;
    }

    private Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }
}