package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PostResponse implements Serializable {
    @SerializedName("status")
    String status;
    @SerializedName("articles")
    List<Post> posts;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
