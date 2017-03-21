package com.github.thomas_p.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.thomas_p.popularmoviesapp.adapters.MovieListAdapter;
import com.github.thomas_p.popularmoviesapp.data.PopularMoviesContract;
import com.github.thomas_p.popularmoviesapp.databinding.ActivityMainBinding;
import com.github.thomas_p.popularmoviesapp.loaders.MovieListAsyncTaskLoader;
import com.github.thomas_p.popularmoviesapp.models.Movie;
import com.github.thomas_p.popularmoviesapp.utils.EndlessRecyclerViewScrollListener;
import com.github.thomas_p.popularmoviesapp.utils.MovieListMode;

import java.text.ParseException;
import java.util.ArrayList;


/**
 * Main page
 */
public class MainActivity extends AppCompatActivity {
    private final String KEY_MOVIE = "movie";
    private final String MOVIE_DETAIL_KEY_IDENTIFIER = "movie_id";

    private ActivityMainBinding mBinding;
    private MovieListAdapter movieAdapter;


    private static final String PAGE_IDENTIFIER = "page";

    private static final int MOVIE_LIST_POPULAR_LOADER_ID = 4337;
    private static final int MOVIE_LIST_TOP_RATED_LOADER_ID = 4667;
    private static final int MOVIE_LIST_FAVORITE_LOADER_ID = 4997;

    private static final String STORED_PAGES_ID = "stored_pages";
    private int storedPages = 0;
    private int actPage = 1;

    /**
     * Network loader manager callbacks
     */
    private final LoaderManager.LoaderCallbacks<Movie[]> networkCallbacks = new LoaderManager.LoaderCallbacks<Movie[]>() {
        @Override
        public Loader<Movie[]> onCreateLoader(int id, Bundle args) {
            showLoadingIndicator();
            int page = 1;
            if (args != null) {
                page = args.getInt(PAGE_IDENTIFIER);
            }
            switch (id) {
                case MOVIE_LIST_POPULAR_LOADER_ID:
                    return new MovieListAsyncTaskLoader(MainActivity.this, MovieListMode.LIST_MODES.POPULAR, page);
                case MOVIE_LIST_TOP_RATED_LOADER_ID:
                    return new MovieListAsyncTaskLoader(MainActivity.this, MovieListMode.LIST_MODES.TOP_RATED, page);
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
            hideLoadingIndicator();
            if (data == null) {
                showErrorMessage();
                return;
            }
            movieAdapter.appendMovieData(data);
            showResultPage();
        }


        @Override
        public void onLoaderReset(Loader<Movie[]> loader) {
            hideLoadingIndicator();

        }

    };

    /**
     * database loader callback
     */
    private final LoaderManager.LoaderCallbacks<Cursor> databaseCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            showLoadingIndicator();
            switch (id) {
                case MOVIE_LIST_FAVORITE_LOADER_ID:
                    Uri listUri = PopularMoviesContract.FavoriteMovieEntry.CONTENT_URI;
                    return new CursorLoader(MainActivity.this, listUri, PopularMoviesContract.FavoriteMovieEntry.PROJECTION, null, null, null);
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            ArrayList<Movie> movies = new ArrayList<>();
            if (data != null) {
                final int BACKDROP_PATH_ID = data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_BACKDROP_PATH);
                final int ID_ID = data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry._ID);
                final int TITLE_ID = data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_TITLE);
                final int POSTER_ID = data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_POSTER_PATH);
                final int OVERVIEW_ID = data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_OVERVIEW);
                final int TAGLINE_ID = data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_TAGLINE);
                final int RELEASE_ID = data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE);
                final int VOTE_ID = data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE);
                final int BUDGET_ID = data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_BUDGET);
                final int RUNTIME_ID = data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_RUNTIME);
                final int HOMEPAGE_ID = data.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.COLUMN_HOMEPAGE);
                if (data.moveToFirst() && data.getCount() > 0) {
                    do {

                        final int id = data.getInt(ID_ID);
                        final Movie movie = new Movie(id);
                        movie.setTitle(data.getString(TITLE_ID));
                        movie.setPathBackdropImage(data.getString(BACKDROP_PATH_ID));
                        movie.setPathPosterImage(data.getString(POSTER_ID));
                        movie.setOverview(data.getString(OVERVIEW_ID));
                        movie.setTagLine(data.getString(TAGLINE_ID));
                        try {
                            movie.setReleaseDate(data.getString(RELEASE_ID));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        movie.setVoteAverage(data.getDouble(VOTE_ID));
                        movie.setBudget(data.getInt(BUDGET_ID));
                        movie.setRuntime(data.getInt(RUNTIME_ID));
                        movie.setHomepage(data.getString(HOMEPAGE_ID));
                        movies.add(movie);
                    } while (data.moveToNext());
                }
            }
            hideLoadingIndicator();
            showResultPage();
            movieAdapter.setMovieListData(movies);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            hideLoadingIndicator();

        }
    };


    MovieListMode movieListMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //
        // set up Try again button
        //
        mBinding.mlErrorPage.actionErrorTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // @// TODO: 01.03.2017 set up retry data load
                loadMovieList(movieListMode.getListMode(), 1);
            }
        });


        final Context context = this;
        final int columns = calculateNoOfColumns(context);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columns);
        //
        // set up RecyclerView
        //
        final RecyclerView mMovieListRecycler = mBinding.rvMovieList;
        final EndlessRecyclerViewScrollListener scrollListener;

        movieAdapter = new MovieListAdapter(new MovieListAdapter.MovieListItemCLickListener() {
            @Override
            public void onClick(Movie movie) {
                Intent detailIntent = new Intent(context, MovieDetailActivity.class);
                detailIntent.putExtra(MOVIE_DETAIL_KEY_IDENTIFIER, movie.getId());
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
                loadMovieList(movieListMode.getListMode(), page + 1);
            }
        };
        mMovieListRecycler.addOnScrollListener(scrollListener);

        //
        // Restore the last list mode
        //


        //
        //  Init movie list mode class
        //
        movieListMode = new MovieListMode(this) {
            @Override
            public void onListModeChanged(LIST_MODES listMode) {
                movieAdapter.clear();
                storedPages = 0;
                loadMovieList(listMode, 1);
            }
        };
        movieListMode.loadListMode(savedInstanceState);
        /*
        mLoadingIndicator = (FrameLayout) findViewById(R.id.ml_loading_indicator);
        */
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_MOVIE)) {
            ArrayList<Movie> tmpMovie = savedInstanceState.getParcelableArrayList(KEY_MOVIE);
            movieAdapter.setMovieListData(tmpMovie);
            storedPages = savedInstanceState.getInt(STORED_PAGES_ID);
        } else {
            loadMovieList(movieListMode.getListMode(), 1);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (movieListMode.getListMode() == MovieListMode.LIST_MODES.FAVORITES) {
            loadMovieList(movieListMode.getListMode(), 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //
        // Menu items
        //
        movieListMode.setActionMenus(
                menu.findItem(R.id.action_sort_by_popular),
                menu.findItem(R.id.action_sort_by_top_rated),
                menu.findItem(R.id.action_sort_by_favorite)
        );
        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_MOVIE, movieAdapter.getMovieListData());
        //
        // store the movie list mode state
        //
        movieListMode.storeListMode(outState);
        outState.putInt(STORED_PAGES_ID, actPage-1);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_popular:
                movieListMode.changeListMode(MovieListMode.LIST_MODES.POPULAR);
                return true;
            case R.id.action_sort_by_top_rated:
                movieListMode.changeListMode(MovieListMode.LIST_MODES.TOP_RATED);
                return true;
            case R.id.action_sort_by_favorite:
                movieListMode.changeListMode(MovieListMode.LIST_MODES.FAVORITES);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Calculate dynamically the number of columns
     * Given by the review
     *
     * @param context
     * @return number of columns to the display
     */
    private static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 160);
    }


    /**
     * load the movie list
     *
     * @param listMode list mode
     * @param page     page, which schould be loaded
     */
    private void loadMovieList(MovieListMode.LIST_MODES listMode, int page) {
        final Bundle args = new Bundle();
        page = page + storedPages;
        actPage = page;
        args.putInt(PAGE_IDENTIFIER, page);
        switch (listMode) {
            case POPULAR:
                getSupportLoaderManager().restartLoader(MOVIE_LIST_POPULAR_LOADER_ID, args, networkCallbacks);
                break;
            case TOP_RATED:
                getSupportLoaderManager().restartLoader(MOVIE_LIST_TOP_RATED_LOADER_ID, args, networkCallbacks);
                break;
            case FAVORITES:
                if (page==1) {
                    getSupportLoaderManager().restartLoader(MOVIE_LIST_FAVORITE_LOADER_ID, args, databaseCallbacks);
                }
                break;
        }
    }

    private void showLoadingIndicator() {
        mBinding.mlLoadingIndicator.loadIndicator.setVisibility(View.VISIBLE);
        mBinding.mlLoadingIndicator.loadIndicatorBackground.setVisibility(View.VISIBLE);
    }

    private void hideLoadingIndicator() {
        mBinding.mlLoadingIndicator.loadIndicator.setVisibility(View.INVISIBLE);
        mBinding.mlLoadingIndicator.loadIndicatorBackground.setVisibility(View.INVISIBLE);
    }


    /**
     *
     */
    private void showErrorMessage() {
        mBinding.mlErrorPage.actionErrorTryAgain.setVisibility(View.VISIBLE);
        mBinding.mlErrorPage.labelErrorNoConnection.setVisibility(View.VISIBLE);
        hideResultPage();
    }


    /**
     *
     */
    private void hideErrorMessage() {
        mBinding.mlErrorPage.actionErrorTryAgain.setVisibility(View.GONE);
        mBinding.mlErrorPage.labelErrorNoConnection.setVisibility(View.GONE);
    }


    /**
     *
     */
    private void showResultPage() {
        mBinding.rvMovieList.setVisibility(View.VISIBLE);
        hideErrorMessage();
    }


    /**
     *
     */
    private void hideResultPage() {
        mBinding.rvMovieList.setVisibility(View.GONE);
    }

}
