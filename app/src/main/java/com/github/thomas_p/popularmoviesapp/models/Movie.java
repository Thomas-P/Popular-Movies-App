package com.github.thomas_p.popularmoviesapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Movie implements Serializable, Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    protected Movie(Parcel in) {
        // not needed, while readSerializable
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this);
    }


    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return (Movie) in.readSerializable();
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
