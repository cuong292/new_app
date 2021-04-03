package com.example.myapplication;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.data.Post;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> posts = new ArrayList<>();
    private OnAction onAction;
    private boolean shouldShowFavorite = true;
    private boolean shouldShowBookMark = true;

    public PostAdapter(OnAction onAction) {
        this.onAction = onAction;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updateData(List<Post> posts) {
        if (posts == null) return;
        this.posts.clear();
        this.posts.addAll(posts);
        notifyDataSetChanged();
    }

    public void shouldShowFavorite(boolean shouldShow) {
        this.shouldShowFavorite = shouldShow;
    }

    public void shouldShowDownload(boolean download) {
        this.shouldShowBookMark = download;
    }

    public void removePost(Post post) {
        this.posts.remove(post);
        notifyDataSetChanged();
    }

    public void updatePost(Post post) {
        int index = posts.indexOf(post);
        posts.set(index, post);
        notifyItemChanged(index);
    }

    public interface OnAction {
        void onLiked(Post post);

        void onDisLiked(Post post);

        void onDownload(Post post);

        void onClick(Post post);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView title;
        private TextView content;
        private TextView time;
        private CheckBox favorite;
        private RequestOptions requestOptions;
        private ImageView bookMark;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.date_time);
            favorite = itemView.findViewById(R.id.favorite);
            bookMark = itemView.findViewById(R.id.bookmark);
            requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        }

        public void bind(Post post) {
            content.setText(post.getContent());
            title.setText(post.getTitle());
            time.setText("Đăng lúc :" + post.getPublishedAt());
            Glide.with(itemView.getContext()).load(post.getUrlToImage()).apply(requestOptions).into(avatar);
            if (shouldShowFavorite) {
                favorite.setVisibility(View.VISIBLE);
                favorite.setOnCheckedChangeListener(null);
                favorite.setChecked(post.liked());
                favorite.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b) onAction.onLiked(post);
                    else onAction.onDisLiked(post);
                });
            } else {
                favorite.setVisibility(View.GONE);
            }

            if (shouldShowBookMark) {
                bookMark.setVisibility(post.stored() || !TextUtils.isEmpty(post.getHtml()) ? View.GONE : View.VISIBLE);
                bookMark.setOnClickListener(v -> onAction.onDownload(post));
            } else {
                bookMark.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(v -> onAction.onClick(post));
            title.setOnClickListener(v -> onAction.onClick(post));
            content.setOnClickListener(v -> onAction.onClick(post));
        }
    }
}