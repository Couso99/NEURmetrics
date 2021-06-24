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

        if (savedInstanceState == null) {
            update_hdr();
            model.nextTest();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (model.previousTest())
            super.onBackPressed();
        else finish();
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
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void initFragmentNavigation() {
        if (model.isUserTrial()) {
            try {
                Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.scoringFragment);
            } catch (java.lang.IllegalArgumentException ignored) {}
        }
        else  {
            try {
                Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(getTestFragment(model.getTest().getTestType()));
            } catch (java.lang.IllegalArgumentException ignored) {}
        }

    }

    private int getTestFragment(int testType) {

        int res = 0;

        switch (model.getTest().getTestType()) {//testType) {
            case 1:
                res = R.id.drawingFragment;
                break;
            case 2:
                res = R.id.tapLettersFragment;
                break;
            case 3:
                res = R.id.imageTestFragment;
                break;
            default:
                res = R.id.imageTestFragment;
                break;
        }

        return res;
    }

    public void nextTest() {

        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(getTestFragment(model.getTest().getTestType()));

        /*if (!model.isUserTrial()) {
            try {
                Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.action_scoringFragment_to_imageTestFragment);
            } catch (java.lang.IllegalArgumentException ignored) {}
        }*/
    }

    public void scoreTest() {

        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.scoringFragment);

    }

    public void trialResults() {
        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.resultsFragment);
    }

    public void update_hdr() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.headers_host_fragment, HeadersFragment.class, null)
                .commit();
    }

}
