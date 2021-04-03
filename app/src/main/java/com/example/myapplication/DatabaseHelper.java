package com.example.myapplication;

import android.content.Context;

import androidx.room.Room;

class DatabaseHelper {
    public static AppDatabase database;

    public static AppDatabase getInstance() {
        return database;
    }

    public static void initDb(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context,
                    AppDatabase.class, "new_db").build();
        }
    }
}
