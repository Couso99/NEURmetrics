package com.imovil.recordapp;


import com.google.gson.JsonElement;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RestService {

    @GET("get-file/{server_file_path}")
    Call<ResponseBody> downloadFile(
            @Path(value="server_file_path",encoded=true) String server_file_path
    );

    /*@GET("get-image/{image}")
    Call<ResponseBody> downloadImage(
            @Path("image") String image
    );

    @GET("get-user-file/{fname}")
    Call<ResponseBody> downloadUserMadeFile(
            @Path(value="fname",encoded=true) String fname
    );*/

    @GET("get-file/general/json/{json_fname}")
    Call<JsonElement> downloadJson(
            @Path("json_fname") String json_fname
    );

    @Multipart
    @POST("upload/{type}")
    Call<ResponseBody> uploadFile(
            @Path("type") String type,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    @GET("get-trials")
    Call<JsonElement> downloadTrialsInfo();

    @GET("get-users")
    Call<JsonElement> downloadUsers();

    @GET("get-user-trials/{userID}")
    Call<JsonElement> downloadTrialsInfoFromUserID(
            @Path("userID") String userID
    );

    @GET("get-trial/{trialID}")
    Call<JsonElement> downloadTrialFromTrialID(
            @Path("trialID") String trialID
    );

    @GET("get-user-trial/{userID}/{start_time}")
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
