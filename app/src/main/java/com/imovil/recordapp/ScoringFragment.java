package com.imovil.recordapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScoringFragment extends Fragment {
    Activity activity;

    File file = null, outputFile=null;

    private Button nextButton;
    //todo solucionar la gochada esta
    PlayableImageView imageView = null;
    RelativeLayout relativeLayout;

    private Test test;
    private int maxScore;
    List<CheckBox> checkBoxList = new ArrayList<CheckBox>();

    boolean isFilename=false, isOutputFilename=false;
    boolean isTrialScored = false;

    public ScoringFragment() {
        // Required empty public constructor
    }

    public static ScoringFragment newInstance(String param1, String param2) {
        ScoringFragment fragment = new ScoringFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scoring, container, false);

        if (getArguments() != null) {
            test = (Test) getArguments().getSerializable("test");
            isTrialScored = (boolean) getArguments().getBoolean("isTrialScored");
            maxScore = test.getMaxScore();
        }

        activity = getActivity();

        nextButton = view.findViewById(R.id.nextButtonScoring);
        nextButton.setOnClickListener(v -> {
            int activeCheckBoxes=0;
            List<Integer> expandedScore = new ArrayList<>();
                for (int i = 0; i < checkBoxList.size(); i++) {
                    if (checkBoxList.get(i).isChecked()) {
                        activeCheckBoxes++;
                        expandedScore.add(1);
                    } else
                        expandedScore.add(0);
                }

            if (maxScore>0) test.setScore(activeCheckBoxes);
            else if (maxScore==0) test.setScore(0);

            test.setExpandedScore(expandedScore);
            ((TrialInterface) activity).nextTest();
        });

        LinearLayout linearLayout = view.findViewById(R.id.linearLayoutCheckboxes);
        List<String> scoreComments = test.getScoreComments();
        String comment;

        List<Integer> expandedScore;
        boolean isExpandedScore=false;
        if ((expandedScore = test.getExpandedScore()) != null && isTrialScored)
            isExpandedScore=true;

        for (int j = 0; j < maxScore; j++) {
            CheckBox btnTag = new CheckBox(activity);
            if (isExpandedScore) btnTag.setChecked(expandedScore.get(j)!=0);
            btnTag.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
            btnTag.setTextSize(32);

            if (scoreComments != null)
            {
                if (scoreComments.size() > j && (comment = scoreComments.get(j)) != null)
                    btnTag.setText(comment);
            }
            else {
                if (maxScore == 1)
                    btnTag.setText("Realizado correctamente");//"Button " + (j + 1 + (i * 4 )));
                else btnTag.setText("");
            }
            btnTag.setId(j + 1);
            checkBoxList.add(btnTag);
            linearLayout.addView(btnTag);
        }

        if (maxScore==0) {
            if (scoreComments != null) {
                for (int i=0;i<scoreComments.size();i++) {
                    CheckBox btnTag = new CheckBox(activity);
                    btnTag.setText(scoreComments.get(i));
                    btnTag.setLayoutParams(new LinearLayout.LayoutParams
                            (LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT));
                    btnTag.setTextSize(32);
                    checkBoxList.add(btnTag);
                    linearLayout.addView(btnTag);
                }
            } else {
                TextView textView = new TextView(activity);
                textView.setText("No hay puntuación para este test");
                textView.setTextSize(32);
                linearLayout.addView(textView);
            }
        }

        relativeLayout = view.findViewById(R.id.relativeLayoutScoring);
        //= null;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1,-1);

        imageView = new PlayableImageView(activity, null);
        imageView.setLayoutParams(layoutParams);

        relativeLayout.addView(imageView);
        String fname, outputfname;
        boolean isDownloadFile=false, isDownloadOutputFile=false;

        if((fname = test.getFilename()) != null) {
            isFilename = true;
            file = new File(((TrialInterface)activity).getFilePath(fname));
            if (!file.exists())
            isDownloadFile = true;
        }

        if(isTrialScored && (outputfname = test.getOutputFilename()) != null) {
            isOutputFilename = true;
            outputFile = new File(((TrialInterface)activity).getFilePath(outputfname));
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
        String testID = test.getTestID();
        int testType = Integer.parseInt(String.valueOf(testID.charAt(0)));
        //ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1,-1);

        switch (testType) {
            case 0:
                //imageView = new PlayableImageView(estaActividad, null, test.getFilename(), test.getOutputFilename());
                //imageView.setLayoutParams(layoutParams);
                imageView.setImageURI(Uri.fromFile(new File(((TrialInterface)activity).getFilePath(test.getFilename()))));
                imageView.setAudio(test.getOutputFilename());
                break;
            case 2:
                if (test.getScore()==1)
                    checkBoxList.get(0).setChecked(true);
            case 1:
                //imageView = new ImageView(estaActividad);
                imageView.setImageURI(Uri.fromFile(new File(((TrialInterface)activity).getFilePath(test.getOutputFilename()))));
                //imageView.setLayoutParams(layoutParams);
                break;
        }
        //relativeLayout.addView(imageView);

    }
}