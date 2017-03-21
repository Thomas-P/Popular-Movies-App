package com.github.thomas_p.popularmoviesapp.loaders;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.github.thomas_p.popularmoviesapp.R;
import com.github.thomas_p.popularmoviesapp.models.Movie;
import com.github.thomas_p.popularmoviesapp.models.MovieListResult;
import com.github.thomas_p.popularmoviesapp.utils.MovieListMode;
import com.github.thomas_p.popularmoviesapp.utils.MovieNetwork;
import com.google.gson.Gson;


public class MovieListAsyncTaskLoader extends AsyncTaskLoader<Movie[]> {

    private int page = 1;
    private MovieListMode.LIST_MODES listMode;
    private MovieNetwork movieNetwork;
    private Movie[] mResult = null;


    public MovieListAsyncTaskLoader(Context context, MovieListMode.LIST_MODES listMode, int page) {
        super(context);
        movieNetwork = new MovieNetwork(context.getString(R.string.api_key));
        this.listMode = listMode;
        this.page = page;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mResult != null) {
            deliverResult(mResult);
        } else {
            forceLoad();
        }
    }

    @Override
    public Movie[] loadInBackground() {
        final String movieListResultString = movieNetwork.getMovieListResult(listMode, page);
        if (movieListResultString == null) {
            return null;
        }
        Gson gson = new Gson();
        final MovieListResult movieListResult = gson.fromJson(movieListResultString, MovieListResult.class);
        mResult = movieListResult.results;
        return mResult;
    }

}
