package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.api.DownloadWeb;
import com.example.myapplication.api.RetrofitBuilder;
import com.example.myapplication.data.Post;
import com.example.myapplication.data.PostResponse;
import com.legendmohe.slidingdrawabletablayout.SlidingDrawableTabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements PostAdapter.OnAction {

    private String searchQuery;
    private RecyclerView rcv;
    private ProgressBar progressBar;
    private PostAdapter adapter;
    private Handler handler;
    private Thread thread;
    private List<Post> posts = new ArrayList<>();
    private SlidingDrawableTabLayout tablayout;
    private EditText searchEdt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Home", "onCreateView: ");
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv = view.findViewById(R.id.post_rv);
        progressBar = view.findViewById(R.id.progress_bar);
        tablayout = view.findViewById(R.id.tablayout);
        adapter = new PostAdapter(this);
        searchEdt = view.findViewById(R.id.query_edt);
        rcv.setAdapter(adapter);
        if (!TextUtils.isEmpty(searchQuery)) {
            getPost(searchQuery);
        }
        tablayout.addTab(tablayout.newTab().setText("Android"));
        tablayout.addTab(tablayout.newTab().setText("IOS"));
        tablayout.addTab(tablayout.newTab().setText("Machine Learning"));
        tablayout.addTab(tablayout.newTab().setText("Artificial Intelligence"));
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 123) {
                    if (thread != null && !thread.isInterrupted()) {
                        thread.interrupt();
                    }
                    showLoading(false);
                } else if (msg.what == 345) {
                    if (thread != null && !thread.isInterrupted()) {
                        thread.interrupt();
                    }
                    if (adapter != null) {
                        adapter.updateData(posts);
                    }
                    showLoading(false);
                } else if (msg.what == 292) {
                    Post post = (Post) msg.obj;
                    storeDatabase(post);
                    adapter.updatePost(post);
                    showLoading(false);
                }
            }
        };
        tablayout.setOnTabSelectedListener(new SlidingDrawableTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(SlidingDrawableTabLayout.Tab tab) {
                getPost(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(SlidingDrawableTabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(SlidingDrawableTabLayout.Tab tab) {

            }
        });

        searchEdt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(adapter != null){
                    adapter.searchPost(s.toString());
                }
            }
        });
        getPost("Android");
    }

    private void storeDatabase(Post post) {
        new Thread(() -> {
            if (post.liked()) {
                DatabaseHelper.database.postDAO().update(post);
            } else {
                DatabaseHelper.database.postDAO().insert(post);
            }

        }).start();
    }

    private void getPost(String query) {
        showLoading(true);
        RetrofitBuilder.getServices().getNewPost(query, "dfc3ae6cc6534f0ebc185e184cd4b335").enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful()) {
                    if (adapter != null && response.body() != null) {
                        mapData(response.body().getPosts());
                    }
                } else {
                    Toast.makeText(getContext(), "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                }
                showLoading(false);
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                showLoading(false);
            }
        });
    }

    private void mapData(List<Post> posts) {
        thread = new Thread(() -> {
            List<Post> allPost = DatabaseHelper.getInstance().postDAO().getPosts();
            for (Post post : posts) {
                for (Post alPost : allPost) {
                    if (post.url.equals(alPost.url)) {
                        post.setLiked(alPost.getLiked());
                        post.setStored(alPost.getStored());
                        post.setHtml(alPost.getHtml());
                        break;
                    }
                }
                this.posts.clear();
                this.posts.addAll(posts);
                Message msg = new Message();
                msg.what = 345;
                handler.sendMessage(msg);
            }

        });
        thread.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Home", "onResume() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Home", "onStop: ");
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    private void showLoading(boolean b) {
        progressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onLiked(Post post) {
        post.setLiked(1);
        showLoading(true);
        thread = new Thread(() -> {
            if (post.stored()) {
                DatabaseHelper.database.postDAO().update(post);
            } else {
                DatabaseHelper.database.postDAO().insert(post);
            }
            Message msg = new Message();
            msg.what = 123;
            handler.sendMessage(msg);
        });
        thread.start();
    }

    @Override
    public void onDisLiked(Post post) {
        post.setLiked(0);
        showLoading(true);
        thread = new Thread(() -> {
            if (post.stored()) {
                DatabaseHelper.database.postDAO().update(post);
            } else {
                DatabaseHelper.database.postDAO().delete(post);
            }
            Message msg = new Message();
            msg.what = 123;
            handler.sendMessage(msg);
        });
        thread.start();
    }

    @Override
    public void onDownload(Post post) {
        showLoading(true);
        post.setStored(1);
        DownloadWeb downloadWeb = new DownloadWeb(handler);
        downloadWeb.execute(post);
    }

    @Override
    public void onClick(Post post) {
        ConnectivityManager cn = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if ((nf == null || !nf.isConnected()) && TextUtils.isEmpty(post.getHtml())) {
            Toast.makeText(getContext(), "Không có kết nối mạng và không có dữ liệu offline", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("post", post);
            getActivity().startActivity(intent);
        }
    }
}
