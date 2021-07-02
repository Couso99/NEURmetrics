package com.imovil.NEURmetrics;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

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
    public final static String GENERAL_IMAGE_BASE_PATH = "general"+File.separator+"image"+File.separator;

    private RestService downloadService;

    private MutableLiveData<Users> usersMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Trials> userTrialsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Trials> newTrialsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Trial> userTrialMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Trial> newTrialMutableLiveData = new MutableLiveData<>();

    WebService() {
        downloadService = ServiceGenerator.createService(RestService.class);
    }

    public Call<ResponseBody> downloadFile(String server_file_path) {
        Call<ResponseBody> call = downloadService.downloadFile(server_file_path);

        return call;
    }

    public void initialize(String deviceID) {
        Call<ResponseBody> call = downloadService.initialize(deviceID);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public Call<ResponseBody> uploadFile(String file_type, RequestBody description, MultipartBody.Part body) {
        Call<ResponseBody> call = downloadService.uploadFile(file_type, description, body);
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

    public void uploadNewUser(User user) {
        JsonElement jsonElement = gson.toJsonTree(user,User.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), String.valueOf(jsonElement));

        MultipartBody.Part body = MultipartBody.Part.createFormData("file","json_file", requestBody);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);

        Call<ResponseBody> call = downloadService.uploadNewUser(description, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
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

    public void updateNewTrial(String trialID) {
        Call<JsonElement> call = downloadService.downloadTrialFromTrialID(trialID);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    List<Trial> trial_list = gson.fromJson(response.body(), new TypeToken<List<Trial>>() {
                    }.getType());
                    if (trial_list.size() > 0) {
                        Trial trial = trial_list.get(0);
                        newTrialMutableLiveData.setValue(trial);
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {}
        });
    }

    public MutableLiveData<Trial> getNewTrial(String trialID) {
        updateNewTrial(trialID);
        return newTrialMutableLiveData;
    }

    public void updateUserTrial(String userID, long startTime) {
        Call<JsonElement> call = downloadService.downloadUserTrial(userID,startTime);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    List<Trial> trial_list = gson.fromJson(response.body(), new TypeToken<List<Trial>>() {
                    }.getType());
                    if (trial_list.size() > 0) {
                        Trial trial = trial_list.get(0);
                        userTrialMutableLiveData.setValue(trial);
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {}
        });
    }

    public MutableLiveData<Trial> getUserTrial(String userID, long startTime) {
        updateUserTrial(userID, startTime);
        return userTrialMutableLiveData;
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