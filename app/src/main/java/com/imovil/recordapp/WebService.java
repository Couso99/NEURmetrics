package com.imovil.recordapp;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    public final static String USER_MADE_BASE_PATH = "user-made"+File.separator;
    public final static String GENERAL_IMAGE_BASE_PATH = "general"+File.separator+"image"+File.separator;

    private RestService downloadService;

    //private MutableLiveData<Image> imageMutableLiveData = new MutableLiveData<>();

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