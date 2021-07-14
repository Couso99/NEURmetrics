package com.imovil.NEURmetrics.ui.trialActivity.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
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

import com.imovil.NEURmetrics.R;
import com.imovil.NEURmetrics.models.Test;
import com.imovil.NEURmetrics.ui.trialActivity.views.PlayableImageView;
import com.imovil.NEURmetrics.ui.trialActivity.TrialActivity;
import com.imovil.NEURmetrics.viewmodels.TrialViewModel;

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

    private Test test;
    private int maxScore;
    List<CheckBox> checkBoxList = new ArrayList<>();
    List<Integer> scoreWeights = new ArrayList<>();

    EditText commentEdit;

    boolean isFilename=false, isOutputFilename=false;
    boolean isTrialScored = false;
    boolean isScoreWeights = false;
    String outputfname;

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
            if (test!=null) model.setTest(test);
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

        nextButton = view.findViewById(R.id.finishedButton);
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

            test.setExpandedScore(expandedScore);
            model.nextTest();
        });

        LinearLayout linearLayout = view.findViewById(R.id.linearLayoutCheckboxes);
        List<String> scoreOptions = test.getScoreOptions();
        String comment;

        List<Integer> expandedScore;
        boolean isExpandedScore=false;
        if ((expandedScore = test.getExpandedScore()) != null)
            isExpandedScore=true;

        // Create checkboxes and check them if needed
        checkBoxList.clear();
        for (int i=0; i< (isExpandedScore ? expandedScore.size() : (scoreOptions==null ? maxScore : scoreOptions.size()));i++) {
            CheckBox btnTag = new CheckBox(activity);
            if (isExpandedScore) btnTag.setChecked(expandedScore.get(i) != 0);
            btnTag.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
            btnTag.setTextSize(18);
            if (scoreOptions != null) btnTag.setText(scoreOptions.get(i));
            else btnTag.setText(R.string.successScore);
            checkBoxList.add(btnTag);
            linearLayout.addView(btnTag);
        }

        relativeLayout = view.findViewById(R.id.relativeLayoutScoring);

        // Choose View according to testType
        switch (test.getTestType()) {
            case TrialActivity.TEST_DRAW_OVER_IMAGE:
            case TrialActivity.TEST_TAP_LETTERS:
            case TrialActivity.TEST_CHECKBOXES:
                imageView = new ImageView(activity,null);
                break;
            case TrialActivity.TEST_RECORD_OVER_IMAGE:
            case TrialActivity.TEST_RECORD_OVER_TEXT:
                imageView = new PlayableImageView(activity, null);
                break;
        }

        relativeLayout.addView(imageView);

        boolean isDownloadFile=false, isDownloadOutputFile=false;

        // Check if parameters files are downloaded
        for (int i=0; i<test.getParametersNumber();i++) {
            if (test.getParametersType().get(i) == "filename") {
                isFilename = true;
                file = new File(model.getFilePath(test.getParameters().get(i)));
                if (!file.exists())
                    isDownloadFile = true;
            }
        }

        // Check if outputs files are downloaded
        for (int i=0; i<test.getOutputsNumber();i++) {
            if (test.getOutputsType().get(i).equals("filename")) {
                isOutputFilename = true;
                outputFile = new File(model.getFilePath(test.getOutputs().get(i)));
                outputfname = test.getOutputs().get(i);
                if (isTrialScored) {
                    if (!outputFile.exists()) isDownloadOutputFile = true;
                }
            }
        }

        // Launch thread to set image when downloaded if not in cache
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

    // update View with image, audio...
    private void loadImageView() {
        ViewGroup.LayoutParams layoutParams;
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        } else {
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        }

        switch (test.getTestType()) {
            case TrialActivity.TEST_TAP_LETTERS:
                if (test.getScore()==1) checkBoxList.get(0).setChecked(true);
            case TrialActivity.TEST_DRAW_OVER_IMAGE:
            case TrialActivity.TEST_CHECKBOXES:
                imageView.setImageURI(Uri.fromFile(new File(model.getFilePath(outputfname))));
                break;
            case TrialActivity.TEST_RECORD_OVER_IMAGE:
                imageView.setImageURI(Uri.fromFile(new File(model.getFilePath(test.getParameters().get(0)))));
                ((PlayableImageView)imageView).setAudio(outputfname);
                //imageView.resource;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    relativeLayout.setForeground(getResources().getDrawable(R.drawable.ic_playbutton));
                }
                break;
            case TrialActivity.TEST_RECORD_OVER_TEXT:
                imageView.setImageURI(Uri.fromFile(new File(model.getFilePath(test.getName() + "_text_parameters_screenshot.jpeg"))));
                ((PlayableImageView)imageView).setAudio(outputfname);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    relativeLayout.setForeground(getResources().getDrawable(R.drawable.ic_playbutton));
                }
                break;

        }
        //imageView.setBackgroundResource(R.drawable.image_border);
        imageView.setLayoutParams(layoutParams);
        imageView.setAdjustViewBounds(true);
    }
}