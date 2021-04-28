package com.imovil.recordapp;

import android.content.Context;
import android.util.Log;

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

public class Repository {
    private final static String TAG = "REPOSITORY";

    private static String FOLDER_PATH;

    private static WebService webService;
    private Context context;

    private boolean isOutputJsonUploaded = false;

    public Repository(Context context) {
        webService = new WebService();
        this.context = context;
        FOLDER_PATH = context.getExternalCacheDir() + File.separator;
    }

    public boolean isOutputJsonUploaded() {
        return isOutputJsonUploaded;
    }

    public String getFilePath(String fname) {
        return FOLDER_PATH + fname;
    }

    public void downloadJson(String fileName) {
        Call<JsonElement> call = webService.downloadJson(fileName);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    ((RepositoryObserver) context).onJsonDownloaded(response.body());
                } else {
                    Log.d(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "error");
            }
        });
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

    public void downloadImage(String fileName){
        File file = new File(context.getExternalCacheDir() + File.separator + fileName);
        if (file.exists()) {
            return;
        }
        String server_path = WebService.GENERAL_IMAGE_BASE_PATH + fileName;
        Call<ResponseBody> call = webService.downloadFile(server_path);
        enqueueWriteResponseBody(call, fileName);
    }

    public void uploadJson(String fname) {
        uploadFile(fname, "application/json", "json");
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
                    isOutputJsonUploaded = true;
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
            File file = new File(context.getExternalCacheDir() + File.separator + fileName);
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

    public void writeJsonToDisk(Tests tests, String fileName) {
        Gson gson =  new GsonBuilder().setPrettyPrinting().create();
        String jsonElement = gson.toJson(tests, Tests.class);

        File file = new File(context.getExternalCacheDir() + File.separator + fileName);

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(jsonElement.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
