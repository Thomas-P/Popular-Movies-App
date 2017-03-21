package com.github.thomas_p.popularmoviesapp.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.github.thomas_p.popularmoviesapp.R;
import com.github.thomas_p.popularmoviesapp.models.Movie;
import com.github.thomas_p.popularmoviesapp.models.MovieReview;
import com.github.thomas_p.popularmoviesapp.models.MovieReviewListResult;
import com.github.thomas_p.popularmoviesapp.models.MovieTrailer;
import com.github.thomas_p.popularmoviesapp.models.MovieTrailerListResult;
import com.github.thomas_p.popularmoviesapp.utils.MovieNetwork;
import com.google.gson.Gson;

public class MovieDetailAsyncTaskLoader extends AsyncTaskLoader<Movie> {
    private Movie mResult;
    private MovieNetwork movieNetwork;
    private int movieId;

    public MovieDetailAsyncTaskLoader(Context context, int movieId) {
        super(context);
        movieNetwork = new MovieNetwork(context.getString(R.string.api_key));
        this.movieId = movieId;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        super.onStartLoading();
        if (mResult != null) {
            deliverResult(mResult);
        } else {
            forceLoad();
        }
    }

    @Override
    public Movie loadInBackground() {
        //
        // movie
        //
        final String movieDetailResultString = movieNetwork.getMovieDetailResult(movieId);
        if (movieDetailResultString==null) {
            mResult = null;
            return null;
        }
        Gson gson = new Gson();
        mResult = gson.fromJson(movieDetailResultString, Movie.class);
        //
        // Trailer
        //
        final String movieTrailersResultString = movieNetwork.getTrailerResult(movieId);
        final MovieTrailerListResult movieTrailerListResult = gson.fromJson(movieTrailersResultString, MovieTrailerListResult.class);
        for (MovieTrailer trailer : movieTrailerListResult.results) {
            if (trailer != null) {
                Log.d("Trailer", trailer.toString());
                mResult.addMovieTrailer(trailer);
            }
        }
        //
        // Review
        //
        final String movieReviewsResultString = movieNetwork.getReviewResult(movieId);
        final MovieReviewListResult movieReviewListResult = gson.fromJson(movieReviewsResultString, MovieReviewListResult.class);
        for (MovieReview review : movieReviewListResult.results) {
            if (review!=null) {
                Log.d("Review", review.toString());
                mResult.addReview(review);
            }
        }
        return mResult;
    }
}
