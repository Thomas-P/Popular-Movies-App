package com.github.thomas_p.popularmoviesapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.github.thomas_p.popularmoviesapp.adapters.MovieListAdapter;
import com.github.thomas_p.popularmoviesapp.models.Movie;
import com.github.thomas_p.popularmoviesapp.utils.EndlessRecyclerViewScrollListener;
import com.github.thomas_p.popularmoviesapp.utils.MovieNetwork;

import org.json.JSONException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mMovieListRecycler;
    private EndlessRecyclerViewScrollListener scrollListener;

    private MovieListAdapter movieAdapter;
    private MovieNetwork.LIST_MODES list_mode;

    // init the movie http loader with the api string
    private MovieNetwork movieNetwork;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list_mode = MovieNetwork.LIST_MODES.POPULAR;

        movieNetwork = new MovieNetwork(getResources().getString(R.string.api_key));


        mMovieListRecycler = (RecyclerView) findViewById(R.id.rv_movie_list);


        final int columns = getResources().getInteger(R.integer.gallery_columns);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columns);


        movieAdapter = new MovieListAdapter();
        mMovieListRecycler.setAdapter(movieAdapter);
        mMovieListRecycler.setLayoutManager(gridLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                new FetchAsyncTask().execute(new LoadParams(list_mode, page));
            }
        };
        mMovieListRecycler.addOnScrollListener(scrollListener);


        new FetchAsyncTask().execute(new LoadParams(list_mode, 1));
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);

    }

    class  LoadParams {
        MovieNetwork.LIST_MODES mode;
        int page;

        LoadParams(MovieNetwork.LIST_MODES mode, int page) {
            this.mode = mode;
            this.page = page;
        }
    }

    class FetchAsyncTask extends AsyncTask<LoadParams, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(LoadParams... params) {
            final LoadParams loadParams = params[0];

            try {
                return movieNetwork.loadPage(loadParams.mode, loadParams.page);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);
            movieAdapter.appendMovieData(movies);
        }
    }
}
