package com.github.thomas_p.popularmoviesapp.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;


public class MovieReview {
    private final String id;
    private String author;
    @SerializedName("content")
    private String review;

    private String url;

    public MovieReview(@NonNull String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
