package com.imovil.recordapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RepositoryObserver{
    private static final String LOG_TAG = "AUDIO_RECORDER";
    private final static String TAG = "WebService";
    private final String jsonFname = "try.json";

    private JsonElement jsonElement;
    //WebService webService;
    Repository repository;

    Button recordButton, stopButton;
    TextView isRecordingView, decibel;
    ImageView imageView;

    ActivityResultLauncher<String> askPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        askPermission.launch(Manifest.permission.RECORD_AUDIO);
        askPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        askPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //webService = new WebService();
        repository = new Repository(this);

        recordButton = findViewById(R.id.recordButton);
        stopButton = findViewById(R.id.stopButton);
        isRecordingView = findViewById(R.id.isRecordingView);
        decibel = findViewById(R.id.decibel);
        imageView = findViewById(R.id.imageView);

        recordButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.recordButton:
                repository.downloadJson(jsonFname);
                //downloadJson(jsonFname);
                break;
            case R.id.stopButton:
                //downloadJson(jsonFname);
                break;
        }
    }

   /* public void downloadJson(String fname) {
        // create upload service client
        RestService service =
                ServiceGenerator.createService(RestService.class);

        Call<JsonElement> call = service.downloadJson(fname);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "server contacted and has file");
                    jsonElement = response.body();

                    Log.d(TAG, "file download was a success? " + jsonElement);
                    init_tests();
                } else {
                    Log.d(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "error");
            }
        });
    }*/

    public void init_tests() {
        Gson gson =  new Gson();
        Tests tests = gson.fromJson(jsonElement, Tests.class);
        Log.d(TAG, String.valueOf(tests));

        List<Test> tests_list = tests.getTests();

        int i = 0;
        for (Test test : tests_list) {
            Log.d(TAG, String.valueOf(i) + ": " + test.getName() + "\t" + test.getFilename());
            i++;
        }

        Intent intent = new Intent(MainActivity.this, TestActivity.class);

        intent.putExtra("tests", tests);
        startActivity(intent);
    }

    @Override
    public void onJsonDownloaded(JsonElement jsonElement) {
        Log.d(TAG, "server contacted and has file");
        this.jsonElement = jsonElement;

        Log.d(TAG, "file download was a success? " + this.jsonElement);
        init_tests();
    }
}