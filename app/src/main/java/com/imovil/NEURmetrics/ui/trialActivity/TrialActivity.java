package com.imovil.NEURmetrics.ui.trialActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.imovil.NEURmetrics.R;
import com.imovil.NEURmetrics.models.Trial;
import com.imovil.NEURmetrics.ui.trialActivity.fragments.HeadersFragment;
import com.imovil.NEURmetrics.viewmodels.TrialViewModel;

public class TrialActivity extends AppCompatActivity {
    private final static String TAG = "TestActivity";

    public final static String ARG_TRIAL = "trial";
    public static final String ARG_TEST = "test";
    public static final String ARG_TRIAL_INFO = "trialInfo";
    public static final String ARG_IS_USER_TRIAL = "isUserTrial";

    public static final int TEST_NEST = 0;
    public static final int TEST_DRAW_OVER_IMAGE = 1;
    public static final int TEST_TAP_LETTERS = 2;
    public static final int TEST_RECORD_OVER_IMAGE = 3;
    public static final int TEST_RECORD_OVER_TEXT = 4;
    public static final int TEST_CHECKBOXES = 5;

    TrialViewModel model;
    Chronometer chronometer;
    TextView scoringLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trial);

        chronometer = findViewById(R.id.recordingChrono);
        scoringLabel = findViewById(R.id.scoringLabel);

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

        model.getIsRecording().observe(this, isRecording -> {
            if (isRecording) {
                if (model.getRecorderBaseTime()==0)
                    chronometer.setBase(SystemClock.elapsedRealtime());
                else
                    chronometer.setBase(model.getRecorderBaseTime());
                chronometer.setVisibility(View.VISIBLE);
                chronometer.start();
            } else {
                chronometer.setVisibility(View.INVISIBLE);
                chronometer.stop();
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

    private int getTestFragment(int testType) {

        int res = TEST_NEST;

        switch (testType) {//testType) {
            case TEST_DRAW_OVER_IMAGE:
                res = R.id.drawingFragment;
                break;
            case TEST_TAP_LETTERS:
                res = R.id.tapLettersFragment;
                break;
            case TEST_RECORD_OVER_IMAGE:
            case TEST_RECORD_OVER_TEXT:
                res = R.id.imageTestFragment;
                break;
            default:
                res = R.id.imageTestFragment;
                break;
        }

        return res;
    }

    public void nextTest() {
        scoringLabel.setVisibility(View.GONE);
        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(getTestFragment(model.getTest().getTestType()));
    }

    public void scoreTest() {
        scoringLabel.setVisibility(View.VISIBLE);
        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.scoringFragment);
    }

    public void trialResults() {
        scoringLabel.setVisibility(View.GONE);
        Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(R.id.resultsFragment);
    }

    public void update_hdr() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.headers_host_fragment, HeadersFragment.class, null)
                .commit();
    }
}
