package com.example.box.util;


import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2018/1/8.
 */

public class HttpUtil {

    //http链接
    public final static int READER_TIMEOUT = 100;
    public final static int WRITE_TIMEOUT = 100;
    public final static int CONNECT_TIMEOUT = 100;


    public final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public final static MediaType IMAGE = MediaType.parse("image/jpeg;charset=utf-8");

    public final static OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(READER_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT,TimeUnit.SECONDS)
            .build();
    public static void sendGetRequestWithHttp (String url,okhttp3.Callback callback){

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);

    }

    public static void sendPostRequestWithHttp(String url , String json, Callback callback){

        RequestBody requestBody = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);

    }


    public static void sendPutImageWithHttp(String url,String localPath,Callback callback){
        File file = new File(localPath);
        RequestBody requestBody = RequestBody.create(IMAGE,file);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);

    }


    public static void sendDeleteWithHttp(String url, String json ,Callback callback){
        RequestBody requestBody = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                .url(url)
                .delete(requestBody)
                .build();
        client.newCall(request).enqueue(callback);

    }





}
