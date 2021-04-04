package com.example.myapplication.api;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.example.myapplication.data.Post;

import java.io.BufferedReader;
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String html = "";
            StringBuilder builder = new StringBuilder();
            while ((html = reader.readLine()) != null) {
                builder.append(html);
            }
            posts[0].setHtml(builder.toString());
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

