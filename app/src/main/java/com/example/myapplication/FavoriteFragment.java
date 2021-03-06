package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.data.Post;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteFragment extends Fragment implements PostAdapter.OnAction {
    private RecyclerView galleryRv;
    private PostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gallery_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Favorite", "onViewCreated() called with: view = [" + view + "], savedInstanceState = [" + savedInstanceState + "]");
        galleryRv = view.findViewById(R.id.post_rv);
        adapter = new PostAdapter(this);
        galleryRv.setAdapter(adapter);
        adapter.shouldShowDownload(false);
        getLikedPost();
    }

    private void getLikedPost() {
        new Thread(() -> {
            List<Post> favorites = DatabaseHelper.getInstance().postDAO().getLikedPost();
            ((Activity) getContext()).runOnUiThread(() -> adapter.updateData(favorites));
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Favorite", "onResume() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Favorite", "onStop: ");
    }

    @Override
    public void onLiked(Post post) {
        // dont have
    }

    @Override
    public void onDisLiked(Post post) {
        if (post.stored()) {
            post.setLiked(0);
            new Thread(() -> DatabaseHelper.database.postDAO().update(post)).start();
        } else {
            new Thread(() -> DatabaseHelper.database.postDAO().delete(post)).start();
        }
        adapter.removePost(post);
    }

    @Override
    public void onDownload(Post post) {

    }

    @Override
    public void onClick(Post post) {
        ConnectivityManager cn = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (!nf.isConnected() && TextUtils.isEmpty(post.getHtml())) {
            Toast.makeText(getContext(), "Kh??ng c?? k???t n???i m???ng v?? kh??ng c?? d??? li???u offline", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("post", post);
            getActivity().startActivity(intent);
        }
    }

}
