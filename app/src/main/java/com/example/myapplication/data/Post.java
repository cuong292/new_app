package com.example.myapplication.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Post implements Serializable {
    @SerializedName("url")
    @PrimaryKey()
    @NonNull
    public String url = "";
    @ColumnInfo(name = "author")
    @SerializedName("author")
    String author;
    @SerializedName("title")
    @ColumnInfo(name = "title")
    String title;
    @SerializedName("description")
    @ColumnInfo(name = "description")
    String description;
    @SerializedName("urlToImage")
    @ColumnInfo(name = "urlToImage")
    String urlToImage;
    @SerializedName("publishedAt")
    @ColumnInfo(name = "publishedAt")
    String publishedAt;
    @SerializedName("content")
    @ColumnInfo(name = "content")
    String content;
    @SerializedName("liked")
    @ColumnInfo(name = "liked")
    Integer liked = 0;
    @SerializedName("stored")
    @ColumnInfo(name = "stored")
    Integer stored = 0;
    @SerializedName("html")
    @ColumnInfo(name = "html")
    String html;

    public boolean liked() {
        return Integer.valueOf(1).equals(liked);
    }

    public Integer getLiked() {
        return liked;
    }

    public Integer getStored() {
        return stored;
    }

    public void setLiked(Integer liked) {
        this.liked = liked;
    }

    public boolean stored() {
        return Integer.valueOf(1).equals(stored);
    }

    public void setStored(Integer stored) {
        this.stored = stored;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        if (post.url.equals(url)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, author, title, description, urlToImage, publishedAt, content, liked, stored, html);
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
