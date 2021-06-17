package com.imovil.recordapp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class TrialActivity extends AppCompatActivity {
    private final static String TAG = "TestActivity";

    public final static String ARG_TRIAL = "trial";
    public static final String ARG_TEST = "test";
    public static final String ARG_TRIAL_INFO = "trialInfo";
    public static final String ARG_IS_USER_TRIAL = "isUserTrial";

    TrialViewModel model;

    int isLastTest = 0, isTestScored=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trial);

        model = new ViewModelProvider(this).get(TrialViewModel.class);

        Bundle b = getIntent().getExtras();

        model.setTrial((Trial) b.getSerializable(ARG_TRIAL));
        model.initTrial();

        model.getIsLaunchScoring().observe(this, isNextFragment -> {
            if (isNextFragment) {
                scoreTest();
                model.setIsLaunchScoring(false);
            }
        });

        model.getIsLaunchNextTest().observe(this, isLaunchTest -> {
            if (isLaunchTest) {
                nextTest();
                model.setIsLaunchNextTest(false);
            }
        });

        model.getIsLaunchTrialResults().observe(this, isLaunchTrialResults -> {
            if (isLaunchTrialResults) {
                trialResults();
                model.setIsLaunchTrialResults(false);
            }
        });

        update_hdr();


        initFragmentNavigation();

        model.nextTest();
    }



    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void initFragmentNavigation() {
        if (model.isUserTrial()) {
            try {
                Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_blankFragment_to_scoringFragment);
            } catch (java.lang.IllegalArgumentException ignored) {}
        }
        else  {
            try {
                Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_blankFragment_to_drawingFragment);
            } catch (java.lang.IllegalArgumentException ignored) {}
        }

    }

   /* private Fragment nextTestNewFragment(Test t, TrialInfo trialInfo) {
        Fragment test_fragment;

        switch (t.getTestType()) {//testType) {
            case 1:
                test_fragment = DrawingFragment.newInstance(t, trialInfo);
                break;
            case 2:
                test_fragment = TapLettersFragment.newInstance(t, trialInfo);
                break;
            case 3:
                test_fragment = ImageTestFragment.newInstance(t, trialInfo);
                observer = (RecorderObserver) test_fragment;
                break;
            default:
                test_fragment = ImageTestFragment.newInstance(t, trialInfo);
                break;
        }

        return test_fragment;
    }*/

    public void nextTest() {
        if (!model.isUserTrial()) {
            try {
                Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_scoringFragment_to_imageTestFragment);
            } catch (java.lang.IllegalArgumentException ignored) {}
        }
    }

    public void scoreTest() {
        if (model.isUserTrial()) {
            try {
                Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_scoringFragment_self);
            } catch (java.lang.IllegalArgumentException ignored) {}
        }
        else if (model.isScoreDuringTests()) {
            try {
                Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_drawingFragment_to_scoringFragment);
            } catch (java.lang.IllegalArgumentException ignored) {}
        }
    }

    public void trialResults() {
        try {
            Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_scoringFragment_to_resultsFragment);
        } catch (java.lang.IllegalArgumentException ignored) {}
    }

    public void update_hdr() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.headers_host_fragment, HeadersFragment.class, null)
                .commit();
    }

}
