package com.example.myapplication.api;

import com.example.myapplication.data.PostResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AppService {
    @GET("everything?q={data}&apiKey={api_key}")
    Call<PostResponse> getNewPost(@Path("data") String data, @Path("api_key") String apiKey);
}
