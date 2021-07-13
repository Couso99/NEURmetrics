package com.imovil.NEURmetrics.remoteDataSource;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.imovil.NEURmetrics.models.Trial;
import com.imovil.NEURmetrics.models.Trials;
import com.imovil.NEURmetrics.models.User;
import com.imovil.NEURmetrics.models.Users;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebService {//implements RestService{
    private final static String TAG = "WebService";

    Gson gson =  new Gson();

    public final static String USER_MADE_BASE_PATH = "user-made"+File.separator;
    public final static String GENERAL_IMAGE_BASE_PATH = "general"+File.separator+"images"+File.separator;

    private RestService downloadService;

    private MutableLiveData<Boolean> isDataUploaded = new MutableLiveData<>();
    private MutableLiveData<Users> usersMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Trials> userTrialsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Trials> newTrialsMutableLiveData = new MutableLiveData<>();

    public WebService() {
        downloadService = ServiceGenerator.createService(RestService.class);
    }

    public void initialize(String deviceID) {
        downloadService.initialize(deviceID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    public void uploadFile(String file_type, RequestBody description, MultipartBody.Part body) {
        downloadService.uploadFile(file_type, description, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    public Call<ResponseBody> downloadFile(String server_file_path) {
        Call<ResponseBody> call = downloadService.downloadFile(server_file_path);
        return call;
    }

    public Call<JsonElement> downloadTrialFromTrialID(String trialID) {
        Call<JsonElement> call = downloadService.downloadTrialFromTrialID(trialID);
        return call;
    }

    public Call<JsonElement> downloadUserTrial(String trialID) {
        Call<JsonElement> call = downloadService.downloadUserTrial(trialID);
        return call;
    }

    public void updateUserTrial(RequestBody description, MultipartBody.Part body) {
        downloadService.updateUserTrial(description, body).enqueue(new Callback<ResponseBody>() {
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

    public void uploadUserTrial(RequestBody description, MultipartBody.Part body) {
        downloadService.uploadUserTrial(description, body).enqueue(new Callback<ResponseBody>() {
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

    private void updateUsers() {
        downloadService.downloadUsers().enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                List<User> users_list = gson.fromJson(response.body(), new TypeToken<List<User>>() {}.getType());
                Users users = new Users(users_list);
                usersMutableLiveData.setValue(users);
                Log.d(TAG, "Users downloaded "+String.valueOf(users_list));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.d(TAG, "Users NOT downloaded"+t.getMessage());
            }
        });
    }

    public MutableLiveData<Users> getUsers() {
        updateUsers();
        return usersMutableLiveData;
    }

    public void uploadNewUser(User user) {
        JsonElement jsonElement = gson.toJsonTree(user,User.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), String.valueOf(jsonElement));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file","json_file", requestBody);

        String descriptionString = "User uploaded, type: application/json";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);

        downloadService.uploadNewUser(description, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    private void updateUserTrials(String userID) {
        downloadService.downloadTrialsInfoFromUserID(userID).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                List<Trial> trials_info_list = gson.fromJson(response.body(), new TypeToken<List<Trial>>() {}.getType());
                Trials trials_info = new Trials(trials_info_list);
                userTrialsMutableLiveData.setValue(trials_info);
                Log.d(TAG, "Trials downloaded: "+String.valueOf(trials_info_list));
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.d(TAG, "Trials NOT downloaded: "+t.getMessage());
            }
        });
    }

    public MutableLiveData<Trials> getUserTrials(String userID) {
        updateUserTrials(userID);
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

    public LiveData<Boolean> getIsDataUploaded() {
        return isDataUploaded;
    }

    public void setIsDataUploaded(boolean isDataUploaded) {
        this.isDataUploaded.setValue(isDataUploaded);
    }

    public boolean isReachable(String host) {
        boolean isReachable = false;

        try {
            InetAddress serverAddr = InetAddress.getByName(host);
            isReachable = serverAddr.isReachable(2000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isReachable;
    }
}