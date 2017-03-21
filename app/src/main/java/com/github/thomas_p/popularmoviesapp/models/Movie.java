package com.github.thomas_p.popularmoviesapp.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class Movie implements Parcelable {
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private static final String PRE_POSTER_PATH = "http://image.tmdb.org/t/p/w185/";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * id settings for movie
     */
    private final int id;
    @SerializedName("imdb_id")
    private String imdbId;
    /**
     * Main information for movie.
     */
    private String title;           // movie title
    @SerializedName("tagline")
    private String tagLine;         // tag line
    private String overview;        // short description
    @SerializedName("release_date")
    private Date releaseDate;       // date of release
    @SerializedName("vote_average")
    private Double voteAverage;     // average voting
    /**
     * Meta information for movie
     */
    @SerializedName("backdrop_path")
    private String pathBackdropImage;   // background image
    @SerializedName("poster_path")
    private String pathPosterImage;     // poster image
    private int budget;                 // budget
    private int revenue;                //
    private String homepage;                // homepage url
    private int runtime;

    private ArrayList<MovieTrailer> movieTrailers;
    private ArrayList<MovieReview> movieReviews;

    public int getId() {
        return id;
    }

    public Movie(int id) {
        this.id = id;
        init();
    }

    private void init() {
        movieTrailers = new ArrayList<>();
        movieReviews = new ArrayList<>();
    }

    protected Movie(Parcel in) {
        init();
        id = in.readInt();
        imdbId = in.readString();

        title = in.readString();
        tagLine = in.readString();
        overview = in.readString();
        // date
        String date = in.readString();
        final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        try {
            releaseDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // end date
        voteAverage = in.readDouble();

        pathBackdropImage = in.readString();
        pathPosterImage = in.readString();


        budget = in.readInt();
        revenue = in.readInt();

        // homepage
        homepage = in.readString();

        // end homepage
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(imdbId);
        dest.writeString(title);
        dest.writeString(tagLine);
        dest.writeString(overview);
        // date
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        dest.writeString(releaseDate != null ? formatter.format(releaseDate) : "");
        // end date
        dest.writeDouble(voteAverage);
        dest.writeString(pathBackdropImage);
        dest.writeString(pathPosterImage);

        dest.writeInt(budget);
        dest.writeInt(revenue);

        dest.writeString(homepage!= null ? homepage.toString() : null);
    }

    //
    //  getter and setter values
    //

    /**
     *
     * @return imdb id
     */
    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public String getRawReleaseDate() {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        return formatter.format(releaseDate);
    }

    public void setReleaseDate(String releaseDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        this.releaseDate = formatter.parse(releaseDate);
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPathBackdropImage() {
        return PRE_POSTER_PATH + pathBackdropImage;
    }

    public void setPathBackdropImage(String pathBackdropImage) {
        this.pathBackdropImage = pathBackdropImage;
    }

    public String getPathPosterImage() {
        return PRE_POSTER_PATH + pathPosterImage;
    }

    public void setPathPosterImage(String pathPosterImage) {
        this.pathPosterImage = pathPosterImage;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public void addMovieTrailer(@NonNull String id, String key, String trailerName) {
        MovieTrailer movieTrailer = new MovieTrailer(id, key, trailerName);
        addMovieTrailer(movieTrailer);
    }

    public void addMovieTrailer(@NonNull MovieTrailer movieTrailer) {
        if (movieTrailers == null) {
            movieTrailers = new ArrayList<>();
        }
        movieTrailers.add(movieTrailer);
    }

    public ArrayList<MovieTrailer> getMovieTrailers() {
        return movieTrailers;
    }

    public void addReview(@NonNull String id, String author, String review) {
        MovieReview movieReview = new MovieReview(id);
        movieReview.setAuthor(author);
        movieReview.setReview(review);
        addReview(movieReview);
    }

    public void addReview(@NonNull MovieReview review) {
        if (movieReviews == null) {
            movieReviews = new ArrayList<>();
        }
        movieReviews.add(review);
    }

    public ArrayList<MovieReview> getMovieReviews() {
        return movieReviews;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getRawPosterPath() {
        return pathPosterImage;
    }

    public String getRawBackdropPath() {
        return pathBackdropImage;
    }
}
