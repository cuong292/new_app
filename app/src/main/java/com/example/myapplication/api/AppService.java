package com.example.myapplication.api;

import com.example.myapplication.data.PostResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AppService {
    @GET("everything")
    Call<PostResponse> getNewPost(@Query("q") String data, @Query("apiKey") String apiKey);
}
