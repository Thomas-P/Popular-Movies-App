package com.github.thomas_p.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.github.thomas_p.popularmoviesapp.adapters.MovieListAdapter;
import com.github.thomas_p.popularmoviesapp.models.Movie;
import com.github.thomas_p.popularmoviesapp.utils.EndlessRecyclerViewScrollListener;
import com.github.thomas_p.popularmoviesapp.utils.MovieNetwork;

import org.json.JSONException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String KEY_MOVIE = "movie";
    private final String KEY_MODE = "key";

    private RecyclerView mMovieListRecycler;
    private EndlessRecyclerViewScrollListener scrollListener;

    private MovieListAdapter movieAdapter;
    private MovieNetwork.LIST_MODES list_mode;

    // init the movie http loader with the api string
    private MovieNetwork movieNetwork;

    private MenuItem mActionPopular;
    private MenuItem mActionTopRated;

    private RelativeLayout mErrorMessage;
    private Button mTryAgain;

    private FrameLayout mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        movieNetwork = new MovieNetwork(getResources().getString(R.string.api_key));

        mErrorMessage = (RelativeLayout) findViewById(R.id.rl_error_message);
        mTryAgain = (Button) findViewById(R.id.action_error_try_again);
        mTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieAdapter.clear();
                loadMovieList(1);
            }
        });

        mMovieListRecycler = (RecyclerView) findViewById(R.id.rv_movie_list);


        final int columns = getResources().getInteger(R.integer.gallery_columns);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columns);

        final Context context = this;

        movieAdapter = new MovieListAdapter(new MovieListAdapter.MovieListItemCLickListener() {
            @Override
            public void onClick(Movie movie) {
                Intent detailIntent = new Intent(context, MovieDetailActivity.class);
                detailIntent.putExtra("movie", (Serializable) movie);
                detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(detailIntent);
            }
        });
        mMovieListRecycler.setHasFixedSize(true);
        mMovieListRecycler.setAdapter(movieAdapter);
        mMovieListRecycler.setLayoutManager(gridLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMovieList(page + 1);
            }
        };
        mMovieListRecycler.addOnScrollListener(scrollListener);


        mLoadingIndicator = (FrameLayout) findViewById(R.id.ml_loading_indicator);
        // load initial state if possible
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_MODE)) {
            byte mode = savedInstanceState.getByte(KEY_MODE);
            list_mode = mode == 0 ? MovieNetwork.LIST_MODES.POPULAR : MovieNetwork.LIST_MODES.TOP_RATED;
        } else {
            list_mode = MovieNetwork.LIST_MODES.POPULAR;
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_MOVIE)) {
            ArrayList<Movie> tmpMovie = savedInstanceState.getParcelableArrayList(KEY_MOVIE);
            movieAdapter.setMovieListData(tmpMovie);
        } else {
            loadMovieList(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mActionPopular = menu.findItem(R.id.action_sort_by_popular);
        mActionTopRated = menu.findItem(R.id.action_sort_by_top_rated);
        setListModeState();
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_MOVIE, movieAdapter.getMovieListData());
        outState.putByte(KEY_MODE, (byte)(list_mode == MovieNetwork.LIST_MODES.POPULAR ? 0 : 1));
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_popular:
                if (list_mode != MovieNetwork.LIST_MODES.POPULAR) {
                    changeListMode(MovieNetwork.LIST_MODES.POPULAR);
                }
                return true;
            case R.id.action_sort_by_top_rated:
                if (list_mode != MovieNetwork.LIST_MODES.TOP_RATED) {
                    changeListMode(MovieNetwork.LIST_MODES.TOP_RATED);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeListMode(MovieNetwork.LIST_MODES mode) {
        list_mode = mode;
        movieAdapter.clear();
        setListModeState();
        loadMovieList(1);
    }
    /**
     * load the movie list
     * @param page page, which schould be loaded
     */
    private void loadMovieList(int page) {
        new FetchAsyncTask().execute(new LoadParams(list_mode, page));
    }


    private void setListModeState() {
        switch (list_mode) {
            case POPULAR:
                mActionTopRated.setChecked(false);
                mActionPopular.setChecked(true);
                break;
            case TOP_RATED:
                mActionPopular.setChecked(false);
                mActionTopRated.setChecked(true);
                break;
        }
    }


    private void setErrorMode(boolean isError) {
        if (isError) {
            mMovieListRecycler.setVisibility(View.INVISIBLE);
            mErrorMessage.setVisibility(View.VISIBLE);
        } else {
            mMovieListRecycler.setVisibility(View.VISIBLE);
            mErrorMessage.setVisibility(View.INVISIBLE);
        }
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
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);
            mLoadingIndicator.setVisibility(View.GONE);
            if (movies != null) {
                movieAdapter.appendMovieData(movies);
            }
            setErrorMode(movies == null);
        }
    }
}
