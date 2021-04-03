package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.api.DownloadWeb;
import com.example.myapplication.api.RetrofitBuilder;
import com.example.myapplication.data.Post;
import com.example.myapplication.data.PostResponse;

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
    private EditText queryEdt;
    private Button searchBtn;
    private ProgressBar progressBar;
    private PostAdapter adapter;
    private Handler handler;
    private Thread thread;
    private List<Post> posts = new ArrayList<>();

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
        queryEdt = view.findViewById(R.id.query_edt);
        searchBtn = view.findViewById(R.id.btn_search);
        progressBar = view.findViewById(R.id.progress_bar);
        queryEdt.setText("");
        searchBtn.setOnClickListener(view1 -> {
            String query = queryEdt.getText().toString();
            if (TextUtils.isEmpty(query)) {
                if (adapter != null) {
                    adapter.updateData(new ArrayList<>());
                }
            }
            getPost(query);
        });
        adapter = new PostAdapter(this);
        rcv.setAdapter(adapter);
        if (!TextUtils.isEmpty(searchQuery)) {
            getPost(searchQuery);
        }
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
        ((MainActivity) getActivity()).setQuery(queryEdt.getText().toString());
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
        if (!nf.isConnected() && TextUtils.isEmpty(post.getHtml())) {
            Toast.makeText(getContext(), "Không có kết nối mạng và không có dữ liệu offline", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("post", post);
            getActivity().startActivity(intent);
        }
    }
}
