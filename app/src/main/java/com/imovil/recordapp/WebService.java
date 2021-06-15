package com.imovil.recordapp;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

public class WebService {//implements RestService{
    private final static String TAG = "WebService";

    Gson gson =  new Gson();

    public final static String USER_MADE_BASE_PATH = "user-made"+File.separator;
    public final static String GENERAL_IMAGE_BASE_PATH = "general"+File.separator+"image"+File.separator;

    private RestService downloadService;

    private MutableLiveData<Users> usersMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Trials> userTrialsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Trials> newTrialsMutableLiveData = new MutableLiveData<>();

    WebService() {
        downloadService = ServiceGenerator.createService(RestService.class);
    }

    public Call<ResponseBody> downloadFile(String server_file_path) {
        Call<ResponseBody> call = downloadService.downloadFile(server_file_path);

        return call;
    }

    public Call<ResponseBody> initialize(String deviceID) {
        Call<ResponseBody> call = downloadService.initialize(deviceID);
        return call;
    }

    public Call<ResponseBody> uploadFile(String file_type, RequestBody description, MultipartBody.Part body) {
        Call<ResponseBody> call = downloadService.uploadFile(file_type, description, body);
        return call;
    }

    public Call<JsonElement> downloadJson(String server_file_path) {
        Call<JsonElement> call = downloadService.downloadJson(server_file_path);

        return call;
    }

    public Call<JsonElement> downloadTrialsList() {
        Call<JsonElement> call = downloadService.downloadTrialsInfo();
        return call;
    }

    public Call<JsonElement> downloadUsers() {
        Call<JsonElement> call = downloadService.downloadUsers();
        return call;
    }

    public Call<JsonElement> downloadTrialsListFromUserID(String userID) {
        Call<JsonElement> call = downloadService.downloadTrialsInfoFromUserID(userID);
        return call;
    }

    public Call<JsonElement> downloadTrialFromTrialID(String trialID) {
        Call<JsonElement> call = downloadService.downloadTrialFromTrialID(trialID);
        return call;
    }

    public Call<JsonElement> downloadUserTrial(String userID, long startTime) {
        Call<JsonElement> call = downloadService.downloadUserTrial(userID, startTime);
        return call;
    }

    public Call<ResponseBody> updateUserTrial(RequestBody description, MultipartBody.Part body) {
        Call<ResponseBody> call = downloadService.updateUserTrial(description, body);
        return call;
    }

    public Call<ResponseBody> uploadUserTrial(RequestBody description, MultipartBody.Part body) {
        Call<ResponseBody> call = downloadService.uploadUserTrial(description, body);
        return call;
    }

    private void updateUsers() {
        downloadService.downloadUsers().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                List<User> users_list = gson.fromJson(response.body(), new TypeToken<List<User>>() {}.getType());
                Users users = new Users(users_list);
                usersMutableLiveData.setValue(users);
                Log.d(TAG, "DESCARGO COSAS"+String.valueOf(users_list));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

                Log.d(TAG, "DESCARGO COSAs: ERROR"+t.getMessage());
            }
        });
    }

    public MutableLiveData<Users> getUsers() {
        updateUsers();
        return usersMutableLiveData;
    }

    private void updateUserTrials(String userID) {
        Call<JsonElement> call = downloadService.downloadTrialsInfoFromUserID(userID);
        Log.d(TAG, "DENTRO DE UPDATE");
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                List<Trial> trials_info_list = gson.fromJson(response.body(), new TypeToken<List<Trial>>() {}.getType());
                Trials trials_info = new Trials(trials_info_list);
                userTrialsMutableLiveData.setValue(trials_info);
                Log.d(TAG, "DESCARGO USERTRIALS"+String.valueOf(trials_info_list));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.d(TAG, "ERROR");

                Log.d(TAG, "DESCARGO COSAs: ERROR"+t.getMessage());
            }
        });
    }

    public MutableLiveData<Trials> getUserTrials(String userID) {
        Log.d(TAG, "USERTRIALS");
        updateUserTrials(userID);
        Log.d(TAG, "USERTRIALS2");
        return userTrialsMutableLiveData;
    }

    private void updateNewTrials() {
        downloadService.downloadTrialsInfo().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                List<Trial> trials_info_list = gson.fromJson(response.body(), new TypeToken<List<Trial>>() {}.getType());
                Trials trials_info = new Trials(trials_info_list);
                newTrialsMutableLiveData.setValue(trials_info);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });
    }

    public MutableLiveData<Trials> getNewTrials() {
        updateNewTrials();
        return newTrialsMutableLiveData;
    }

    public boolean isReachable() {
        boolean isReachable = false;

        try {
            InetAddress serverAddr = InetAddress.getByName("192.168.0.21");
            isReachable = serverAddr.isReachable(2000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isReachable;
    }


    /*public Call<ResponseBody> downloadUserMadeFile(String fileName) {
        Call<ResponseBody> call = downloadService.downloadUserMadeFile(fileName);

        return call;
    }

    public void downloadImage(String fileName){
        RestService downloadService = ServiceGenerator.createService(RestService.class);

        File file = new File(context.getExternalCacheDir() + File.separator + fileName);
        if (file.exists()) {
            return;
        }

        enqueueWriteResponseBody(downloadService.downloadImage(fileName), fileName);
    }*/

}