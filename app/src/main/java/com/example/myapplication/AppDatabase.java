package com.example.myapplication;

import com.example.myapplication.data.Post;
import com.example.myapplication.data.PostDAO;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Post.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PostDAO postDAO();
}
