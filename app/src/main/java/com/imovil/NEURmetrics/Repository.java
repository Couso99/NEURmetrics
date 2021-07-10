package com.imovil.NEURmetrics;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private final static String TAG = "REPOSITORY";

    private static String FOLDER_PATH;

    private static WebService webService;
    private Context context;

    private LiveData<Users> users;
    private LiveData<Trials> userTrials;
    private LiveData<Trials> newTrials;
    private static Trial trial;
    private MutableLiveData<Boolean> isTrialDownloaded = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDataUploaded = new MutableLiveData<>();

    public Repository(Context context) {
        this.context = context;
        updateBaseURL();
        FOLDER_PATH = context.getExternalCacheDir() + File.separator;
    }

    public void updateBaseURL() {
        boolean isChanged = false;

        if (webService!=null) {
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
            String host = SP.getString("server_ip","localhost");
            String port = SP.getString("port", "80");
            String url  = ("http://"+host+port);
            isChanged = ServiceGenerator.setBaseUrl(url);
        }

        if (webService==null || isChanged) webService = new WebService();
    }

    public String getFilePath(String fname) {
        return FOLDER_PATH + fname;
    }

    public void downloadUserMadeFile(String fileName) {
        File file = new File(FOLDER_PATH + fileName);
        if (file.exists()) {
            return;
        }
        String server_path = WebService.USER_MADE_BASE_PATH + fileName;
        Call<ResponseBody> call = webService.downloadFile(server_path);
        enqueueWriteResponseBody(call, fileName);
    }

    public void downloadFile(String fileName){
        File file = new File(FOLDER_PATH + fileName);
        if (file.exists()) {
            return;
        }
        String server_path = WebService.GENERAL_IMAGE_BASE_PATH + fileName;
        Call<ResponseBody> call = webService.downloadFile(server_path);
        enqueueWriteResponseBody(call, fileName);
    }

    public void initialize_device() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        String deviceID = SP.getString("device_id", "device0000");

        webService.initialize(deviceID);
    }

    public void uploadGeneral(String fname, String mediaType) {
        uploadFile(fname, mediaType, "general");
    }

    public void uploadFile(String fileName, String mediaType, String server_file_path) {
        File file = new File(fileName);

        // create RequestBody instance from file
        RequestBody requestFile =
                //RequestBody.create(MediaType.parse("image/*"), file);
                //RequestBody.create(MediaType.parse("audio/*"),file);
                //RequestBody.create(MediaType.parse("application/json"),file);
                RequestBody.create(MediaType.parse(mediaType),file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                  okhttp3.MultipartBody.FORM, descriptionString);

        Call<ResponseBody> call = webService.uploadFile(server_file_path, description, body);

        // finally, execute the request
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload", "success");
                if (server_file_path=="json")
                    isDataUploaded.setValue(true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    public void updateUserTrial(Trial trial) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(trial,Trial.class);
        updateUserTrial(jsonElement);
    }

    public void updateUserTrial(JsonElement jsonElement) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), String.valueOf(jsonElement));

        MultipartBody.Part body = MultipartBody.Part.createFormData("file","json_file", requestBody);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);

        Call<ResponseBody> call = webService.updateUserTrial(description, body);

        // finally, execute the request
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload", "success");
                isDataUploaded.setValue(true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    public void uploadUserTrial(Trial trial) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(trial,Trial.class);
        uploadUserTrial(jsonElement);
    }

    public void uploadUserTrial(JsonElement jsonElement) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), String.valueOf(jsonElement));

        MultipartBody.Part body = MultipartBody.Part.createFormData("file","json_file", requestBody);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);

        Call<ResponseBody> call = webService.uploadUserTrial(description, body);

        // finally, execute the request
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload", "success");
                isDataUploaded.setValue(true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    private void enqueueWriteResponseBody(Call<ResponseBody> call, String fileName) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "server contacted and has file");
                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(), fileName);
                    Log.d(TAG, "file download was a success? " + writtenToDisk);
                } else {
                    Log.d(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "error");
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            File file = new File(FOLDER_PATH + fileName);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public void updateUsersData() {
        this.users = webService.getUsers();
    }

    public LiveData<Users> getUsers() {
        return users;
    }

    public void updateUserTrials(String userID) {
        this.userTrials = webService.getUserTrials(userID);
    }

    public LiveData<Trials> getUserTrials() {
        return userTrials;
    }

    public void updateNewTrials() {
        this.newTrials = webService.getNewTrials();
    }

    public LiveData<Trials> getNewTrials() {
        return newTrials;
    }

    public void downloadUserTrial(String userID, long startTime) {
        Call<JsonElement> call = webService.downloadUserTrial(userID, startTime);

        // finally, execute the request
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Gson gson = new Gson();
                List<Trial> trial_list = gson.fromJson(response.body(), new TypeToken<List<Trial>>() {
                }.getType());
                if (trial_list.size() > 0) {
                    trial = trial_list.get(0);
                    isTrialDownloaded.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {}
            });
    }

    public void downloadNewTrial(String trialID) {
        Call<JsonElement> call = webService.downloadTrialFromTrialID(trialID);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Gson gson = new Gson();
                List<Trial> trial_list = gson.fromJson(response.body(), new TypeToken<List<Trial>>() {
                }.getType());
                if (trial_list == null) return;

                if (!trial_list.isEmpty() && trial_list.size() > 0) {
                    trial = trial_list.get(0);
                    isTrialDownloaded.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {

            }
        });
    }

    public MutableLiveData<Boolean> isTrialDownloaded() {
        return isTrialDownloaded;
    }

    public void setIsTrialDownloaded(boolean isTrialDownloaded) {
        this.isTrialDownloaded.setValue(isTrialDownloaded);
    }

    public Trial getTrial() {
        return trial;
    }

    public boolean isReachable() {

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        String host = SP.getString("server_ip","");

        if (host.isEmpty()) return false;

        return webService.isReachable(host);
    }


    public LiveData<Boolean> isDataUploaded() {
        return isDataUploaded;
    }

    public void setIsDataUploaded(MutableLiveData<Boolean> isDataUploaded) {
        this.isDataUploaded = isDataUploaded;
    }

    public void uploadNewUser(User user) {
        webService.uploadNewUser(user);
    }
}
