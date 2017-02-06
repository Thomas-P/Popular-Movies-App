package com.github.thomas_p.popularmoviesapp.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Thomas-P on 05.02.2017.
 */


public class Movie implements Serializable {
    private static String PRE_POSTER_PATH = "http://image.tmdb.org/t/p/w185/";
    private boolean isAdult;
    private String posterPath;
    private String overview;
    private Date releaseDate;
    private Double voteAverage;



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
        return PRE_POSTER_PATH + this.posterPath;
    }

    public void setOverview(String synopsis) {
        overview = synopsis;
    }

    public String getOverview() {
        return overview;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Double getVoteAverage() {
        return  voteAverage;
    }
}
