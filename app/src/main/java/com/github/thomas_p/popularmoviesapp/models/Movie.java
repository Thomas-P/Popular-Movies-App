package com.github.thomas_p.popularmoviesapp.models;

/**
 * Created by Thomas-P on 05.02.2017.
 */


public class Movie {
    private static String PRE_POSTER_PATH = "http://image.tmdb.org/t/p/w185/";
    private boolean isAdult;
    private String posterPath;
    private String overview;
    private String releaseDate;

    private String title;


    private int id;


    public Movie(int id, String title, String posterPath) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getPoster() {
        return this.posterPath;
    }
}
