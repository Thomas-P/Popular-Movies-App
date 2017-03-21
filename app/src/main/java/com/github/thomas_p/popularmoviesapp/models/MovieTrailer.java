package com.github.thomas_p.popularmoviesapp.models;

import android.net.Uri;

/**
 * Created by Thomas-P on 27.02.2017.
 */

public class MovieTrailer {
    private static final String URL_YOUTUBE = "https://youtube.com";
    private final String id;
    private final String key;

    private String name;

    public MovieTrailer(String id, String key, String name) {
        this.id = id;
        this.key = key;
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getLink() {
        return Uri
                .parse(URL_YOUTUBE)
                .buildUpon()
                .appendPath("watch")
                .appendQueryParameter("v", key)
                .build();
    }

}
