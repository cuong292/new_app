package com.example.myapplication.pref;

import android.content.Context;
import android.content.SharedPreferences;

class MyPref {
    private static SharedPreferences mPref;

    public static SharedPreferences getInstance(Context context) {
        if (mPref == null) {
            mPref = context.getSharedPreferences("app-doc-bao", Context.MODE_PRIVATE);
        }
        return mPref;
    }
}
