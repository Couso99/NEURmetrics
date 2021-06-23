package com.imovil.recordapp;


import com.google.gson.JsonElement;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RestService {

    @GET("file/{server_file_path}")
    Call<ResponseBody> downloadFile(
            @Path(value="server_file_path",encoded=true) String server_file_path
    );

    @POST("initialize/{deviceID}")
    Call<ResponseBody> initialize(
            @Path("deviceID") String deviceID
    );

    /*@GET("get-file/general/json/{json_fname}")
    Call<JsonElement> downloadJson(
            @Path("json_fname") String json_fname
    );*/

    @Multipart
    @POST("file/{type}")
    Call<ResponseBody> uploadFile(
            @Path("type") String type,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    @Multipart
    @PATCH("/user-trials")
    Call<ResponseBody> updateUserTrial(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("/user-trials")
    Call<ResponseBody> uploadUserTrial(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    @GET("trials")
    Call<JsonElement> downloadTrialsInfo();

    @GET("users")
    Call<JsonElement> downloadUsers();

    @POST("users")
    Call<ResponseBody> uploadNewUser(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    @GET("user-trials/{userID}")
    Call<JsonElement> downloadTrialsInfoFromUserID(
            @Path("userID") String userID
    );

    @GET("trial/{trialID}")
    Call<JsonElement> downloadTrialFromTrialID(
            @Path("trialID") String trialID
    );

    @GET("user-trials/{userID}/{start_time}")
    Call<JsonElement> downloadUserTrial(
            @Path("userID") String userID,
            @Path("start_time") long start_time
    );

    /*@Multipart
    @POST("upload-general")
    Call<ResponseBody> uploadGeneral(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("upload-json")
    Call<ResponseBody> uploadJson(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );*/
}
