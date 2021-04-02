package com.example.myapplication.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RetrofitBuilder {
    private static final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setVersion(1.0)
            .create();

    private static AppService appService;

    public static AppService getServices() {
        if (appService == null) {
            OkHttpClient.Builder client = okHttpClient.newBuilder();
            client.addInterceptor(new HttpLoggingInterceptor());
            Retrofit mRetrofit = new Retrofit.Builder()
                    .client(client.build())
                    .baseUrl("https://newsapi.org/v2/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            appService = mRetrofit.create(AppService.class);
        }
        return appService;
    }
}
