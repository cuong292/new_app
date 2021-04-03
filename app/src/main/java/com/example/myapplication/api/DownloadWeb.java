package com.example.myapplication.api;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.myapplication.data.Post;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadWeb extends AsyncTask<Post, Void, Post> {

    private final Handler handler;

    public DownloadWeb(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected Post doInBackground(Post... posts) {
        String result = "";
        URL url;
        HttpURLConnection urlConnection;

        try {
            url = new URL(posts[0].getUrl());
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();

            while (data != -1) {
                char current = (char) data;
                result = result + current;
                data = reader.read();
                Log.d("cuongnb", "doInBackground() called with: posts = [" + data + "]");
            }
            posts[0].setHtml(result);
            return posts[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Post s) {
        super.onPostExecute(s);
        Message message = new Message();
        message.what = 292;
        message.obj = s;
        handler.sendMessage(message);
    }
}

