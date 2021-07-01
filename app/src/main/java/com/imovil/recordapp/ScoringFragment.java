package com.imovil.recordapp;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScoringFragment extends Fragment {
    private static final String ARG_TEST = "test";
    private static final String ARG_IS_TRIAL_SCORED = "isTrialScored";

    Activity activity;

    TrialViewModel model;

    File file = null, outputFile=null;

    private Button nextButton;

    ImageView imageView = null;
    RelativeLayout relativeLayout;

    ElegantNumberButton elegantNumberButton = null;

    private Test test;
    private int maxScore;
    List<CheckBox> checkBoxList = new ArrayList<>();
    List<Integer> scoreWeights = new ArrayList<>();

    EditText commentEdit;

    int elegantNumber;

    boolean isFilename=false, isOutputFilename=false;
    boolean isTrialScored = false;
    boolean isScoreWeights = false;

    public ScoringFragment() {
        // Required empty public constructor
    }

    public static ScoringFragment newInstance(Test test, boolean isTrialScored) {
        ScoringFragment fragment = new ScoringFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TEST, test);
        args.putBoolean(ARG_IS_TRIAL_SCORED, isTrialScored);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(TrialViewModel.class);

        if (getArguments() != null) {
            test = (Test) getArguments().getSerializable(ARG_TEST);
            isTrialScored = getArguments().getBoolean(ARG_IS_TRIAL_SCORED);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scoring, container, false);

        test = model.getTest();
        isTrialScored = model.isUserTrial();
        maxScore = test.getMaxScore();
        scoreWeights = test.getScoreWeights();
        if (scoreWeights != null)
            isScoreWeights = true;

        activity = getActivity();

        commentEdit = view.findViewById(R.id.editTextTextMultiLine);
        if (test.getComment()!=null) commentEdit.setText(test.getComment());

        commentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                test.setComment(s.toString());
            }
        });

        nextButton = view.findViewById(R.id.nextButtonScoring);
        nextButton.setOnClickListener(v -> {
            int activeCheckBoxes=0;
            List<Integer> expandedScore = new ArrayList<>();
            for (int i = 0; i < checkBoxList.size(); i++) {
                if (checkBoxList.get(i).isChecked()) {
                    activeCheckBoxes+=(isScoreWeights ? scoreWeights.get(i) : 1);
                    expandedScore.add(isScoreWeights ? scoreWeights.get(i) : 1);
                } else
                    expandedScore.add(0);
            }

            if (maxScore!=0) test.setScore(activeCheckBoxes);
            else test.setScore(0);

            if (elegantNumberButton != null) {
                    test.setScore(elegantNumber);
            }

            test.setExpandedScore(expandedScore);
            model.nextTest();
        });

        LinearLayout linearLayout = view.findViewById(R.id.linearLayoutCheckboxes);
        List<String> scoreOptions = test.getScoreOptions();
        String comment;

        List<Integer> expandedScore;
        boolean isExpandedScore=false;
        if ((expandedScore = test.getExpandedScore()) != null && isTrialScored)
            isExpandedScore=true;

        if (maxScore>=0) {
            for (int j = 0; j < maxScore && ((scoreOptions == null) || (j < scoreOptions.size())); j++) {
                CheckBox btnTag = new CheckBox(activity);
                if (isExpandedScore) btnTag.setChecked(expandedScore.get(j) != 0);
                btnTag.setLayoutParams(new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));
                btnTag.setTextSize(18);

                if (scoreOptions != null) {
                    if (scoreOptions.size() > j && (comment = scoreOptions.get(j)) != null)
                        btnTag.setText(comment);
                } else {
                    if (maxScore == 1)
                        btnTag.setText(R.string.successScore);//"Button " + (j + 1 + (i * 4 )));
                    else btnTag.setText("");
                }
                btnTag.setId(j + 1);
                checkBoxList.add(btnTag);
                linearLayout.addView(btnTag);
            }
        }

        else if (maxScore==-1) {
            if (scoreOptions != null) {
                for (int i = 0; i < scoreOptions.size(); i++) {
                    CheckBox btnTag = new CheckBox(activity);
                    btnTag.setText(scoreOptions.get(i));
                    btnTag.setLayoutParams(new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT));
                    btnTag.setTextSize(18);
                    checkBoxList.add(btnTag);
                    linearLayout.addView(btnTag);
                }
            } else {
                elegantNumberButton = new ElegantNumberButton(activity);
                elegantNumberButton.setLayoutParams(new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));
                elegantNumberButton.setOnValueChangeListener((view1, oldValue, newValue) -> elegantNumber = newValue);

                linearLayout.addView(elegantNumberButton);
            }
        }

        relativeLayout = view.findViewById(R.id.relativeLayoutScoring);

        switch (test.getTestType()) {
            case 1:
            case 2:
                imageView = new ImageView(activity,null);
                break;
            case 3:
                imageView = new PlayableImageView(activity, null);
        }

        relativeLayout.addView(imageView);
        String fname, outputfname;
        boolean isDownloadFile=false, isDownloadOutputFile=false;

        if(test.getParametersNumber()!=0 && test.getParametersType().get(0).equals("filename") && (fname = test.getParameters().get(0)) != null) {
            isFilename = true;
            file = new File(model.getFilePath(fname));
            if (!file.exists())
            isDownloadFile = true;
        }

        if(isTrialScored && (outputfname = test.getOutputFilename()) != null) {
            isOutputFilename = true;
            outputFile = new File(model.getFilePath(outputfname));
            if (!outputFile.exists()) isDownloadOutputFile = true;
        }

        if (isDownloadFile || isDownloadOutputFile) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    boolean isFileDownloaded = !isFilename;
                    boolean isOutputFileDownloaded = !isOutputFilename;

                    try {
                        while(!isFileDownloaded || !isOutputFileDownloaded) {
                            sleep(200);
                            if (file!=null&&file.exists()) isFileDownloaded = true;
                            if (outputFile != null &&outputFile.exists()) isOutputFileDownloaded = true;
                        }
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }

                    activity.runOnUiThread(() -> loadImageView());
                    }
            };

            thread.start();
        } else loadImageView();

        return view;
    }

    private void loadImageView() {
        ViewGroup.LayoutParams layoutParams;
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        } else {
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        }

        switch (test.getTestType()) {
            case 2:
                if (test.getScore()==1) checkBoxList.get(0).setChecked(true);
            case 1:
                imageView.setImageURI(Uri.fromFile(new File(model.getFilePath(test.getOutputFilename()))));
                break;
            case 3:
                imageView.setImageURI(Uri.fromFile(new File(model.getFilePath(test.getParameters().get(0)))));
                ((PlayableImageView)imageView).setAudio(test.getOutputFilename());
                break;
        }
        imageView.setBackgroundResource(R.drawable.image_border);
        imageView.setLayoutParams(layoutParams);
        imageView.setAdjustViewBounds(true);
    }
}