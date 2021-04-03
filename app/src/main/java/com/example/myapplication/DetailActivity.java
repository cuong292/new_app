package com.example.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

import com.example.myapplication.data.Post;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private WebView webview;
    private Post post;
    private ImageView backIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        webview = findViewById(R.id.webview);
        if (getIntent() != null && getIntent().getExtras() != null) {
            post = (Post) getIntent().getExtras().getSerializable("post");
        }
        backIv = findViewById(R.id.back_btn);
        backIv.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf.isConnected()) {
            webview.loadUrl(post.getUrl());
        } else {
            webview.loadDataWithBaseURL("", post.getHtml(), "text/html; charset=utf-8", "utf-8", "");
        }
    }
}
