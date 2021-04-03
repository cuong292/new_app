package com.example.myapplication.data;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface PostDAO {
    @Query("SELECT * FROM Post WHERE liked = 1")
    List<Post> getLikedPost();

    @Query("SELECT * FROM Post WHERE stored = 1")
    List<Post> getStoredPost();

    @Query("SELECT * FROM Post WHERE url = (:url)")
    Post getPost(String url);

    @Insert
    void insert(Post post);

    @Update
    void update(Post post);

    @Delete
    void delete(Post post);

    @Query("SELECT * FROM Post")
    List<Post> getPosts();
}
