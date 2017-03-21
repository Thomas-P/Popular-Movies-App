package com.github.thomas_p.popularmoviesapp;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.thomas_p.popularmoviesapp.adapters.MovieReviewListAdapter;
import com.github.thomas_p.popularmoviesapp.adapters.MovieTrailerListAdapter;
import com.github.thomas_p.popularmoviesapp.data.PopularMoviesContract;
import com.github.thomas_p.popularmoviesapp.databinding.ActivityMovieDetailBinding;
import com.github.thomas_p.popularmoviesapp.loaders.MovieDetailAsyncTaskLoader;
import com.github.thomas_p.popularmoviesapp.models.Movie;
import com.github.thomas_p.popularmoviesapp.models.MovieTrailer;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MovieDetailActivity extends AppCompatActivity implements
        MovieTrailerListAdapter.onClickListener, LoaderManager.LoaderCallbacks<Movie> {


    private final static String KEY_IDENTIFIER = "key_detail_movie";
    private final String MOVIE_DETAIL_KEY_IDENTIFIER = "movie_id";

    private final int MOVIE_DETAIL_LOADER_ID = 8997;

    private ActivityMovieDetailBinding mBinding;
    private MovieTrailerListAdapter movieTrailerListAdapter;
    private MovieReviewListAdapter movieReviewListAdapter;

    private Movie actMovie;
    private int movieId;
    boolean isFavorite = false;


    private LoaderManager.LoaderCallbacks<Cursor> getByDatabaseCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                    MovieDetailActivity.this,
                    PopularMoviesContract.FavoriteMovieEntry.getFavoriteMovie(movieId),
                    PopularMoviesContract.FavoriteMovieEntry.PROJECTION,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data == null) {
                isFavorite = false;
                return;
            }
            isFavorite = data.getCount() > 0;
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
        mBinding.mlErrorPage.labelErrorNoConnection.setVisibility(View.INVISIBLE);
        mBinding.mlErrorPage.actionErrorTryAgain.setVisibility(View.INVISIBLE);
        //
        // star movie
        //
        mBinding.mdHeader.actionFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    getContentResolver().delete(PopularMoviesContract.FavoriteMovieEntry.getFavoriteMovie(movieId), null, null);
                    isFavorite = false;
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(PopularMoviesContract.FavoriteMovieEntry._ID, movieId);
                    cv.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_BACKDROP_PATH, actMovie.getRawBackdropPath());
                    cv.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_TITLE, actMovie.getTitle());
                    cv.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_TAGLINE, actMovie.getTagLine());
                    cv.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_OVERVIEW, actMovie.getOverview());
                    cv.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, actMovie.getRawReleaseDate());
                    cv.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE, actMovie.getVoteAverage());
                    cv.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_POSTER_PATH, actMovie.getRawPosterPath());
                    cv.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_BUDGET, actMovie.getBudget());
                    cv.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_REVENUE, actMovie.getRevenue());
                    cv.put(PopularMoviesContract.FavoriteMovieEntry.COLUMN_RUNTIME, actMovie.getRuntime());
                    getContentResolver().insert(PopularMoviesContract.FavoriteMovieEntry.CONTENT_URI, cv);
                    isFavorite = true;
                }
                setFavorite();
            }
        });


        movieTrailerListAdapter = new MovieTrailerListAdapter(this);
        movieReviewListAdapter = new MovieReviewListAdapter();

        final RecyclerView rvMdTrailerList = mBinding.mdTrailers.rvMdTrailerList;
        final RecyclerView rvMdReviewList = mBinding.mdReviews.rvMdReviewList;

        rvMdTrailerList.setAdapter(movieTrailerListAdapter);
        rvMdTrailerList.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        rvMdReviewList.setAdapter(movieReviewListAdapter);
        rvMdReviewList.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        Intent intent = getIntent();
        movieId = intent.getIntExtra(MOVIE_DETAIL_KEY_IDENTIFIER, 0);
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_IDENTIFIER)) {
            actMovie = savedInstanceState.getParcelable(KEY_IDENTIFIER);
            if (actMovie != null) {
                if (movieId == actMovie.getId()) {
                    setMovieData(actMovie);
                } else {
                    actMovie = null;
                }
            }
        }
        startLoading();
        mBinding.mlErrorPage.actionErrorTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoading();
            }
        });
    }

    private void startLoading() {
        if (actMovie == null) {
            // @// TODO: 20.03.2017 load from http or database
            getSupportLoaderManager().restartLoader(MOVIE_DETAIL_LOADER_ID, null, this);
        }
        getSupportLoaderManager().restartLoader(0, null, getByDatabaseCallback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_IDENTIFIER, actMovie);
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("DefaultLocale")
    private void setMovieData(@NonNull Movie movie) {
        setFavorite();

        mBinding.mdHeader.filmTitle.setText(movie.getTitle());


        mBinding.mdMetaData.filmTagLine.setText(movie.getTagLine());
        mBinding.mdDescription.filmDescription.setText(movie.getOverview());
        // date
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.config_date_format), Locale.getDefault());
        final Date date = movie.getReleaseDate();
        mBinding.mdHeader.releaseDate.setText(formatter.format(date));

        mBinding.mdMetaData.vote.setText(String.format("%.1f", movie.getVoteAverage()));

        mBinding.mdMetaData.playTime.setText(movie.getRuntime() + " " + getString(R.string.md_playtime_minutes));

        movieTrailerListAdapter.setMovieTrailerList(movie.getMovieTrailers());
        movieReviewListAdapter.setMovieReviews(movie.getMovieReviews());


        Picasso.with(this)
                .load(movie.getPathBackdropImage())
                .into(mBinding.mdHeader.filmBackground);

        Picasso.with(this)
                .load(movie.getPathPosterImage())
                .into(mBinding.mdMetaData.filmPoster);
    }

    private void setFavorite() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBinding.mdHeader.actionFav.setImageDrawable(isFavorite ? getDrawable(android.R.drawable.star_big_on) : getDrawable(android.R.drawable.star_big_off));
        } else {
            // http://stackoverflow.com/questions/27796246/android-alternative-for-context-getdrawable
            mBinding.mdHeader.actionFav.setImageDrawable(isFavorite ? ContextCompat.getDrawable(this, android.R.drawable.star_big_on) : ContextCompat.getDrawable(this, android.R.drawable.star_big_off));
        }
    }

    @Override
    public void onTrailerClick(MovieTrailer movieTrailer) {
        // @// DONE: 27.02.2017 create an intent and open youtube or browser
        //
        // uses the answer of http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
        //
        try {
            final Uri internalYoutube = Uri.parse("vnd.youtube:" + movieTrailer.getKey());
            final Intent appIntent = new Intent(Intent.ACTION_VIEW, internalYoutube);
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            final Intent webIntent = new Intent(Intent.ACTION_VIEW, movieTrailer.getLink());
            startActivity(webIntent);
        }
    }


    @Override
    public Loader<Movie> onCreateLoader(int id, Bundle args) {
        setPageMode(View.INVISIBLE);
        mBinding.mlLoadingIndicator.loadIndicatorBackground.setVisibility(View.VISIBLE);
        mBinding.mlLoadingIndicator.loadIndicator.setVisibility(View.VISIBLE);
        switch (id) {
            case MOVIE_DETAIL_LOADER_ID:
                return new MovieDetailAsyncTaskLoader(this, movieId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie data) {
        mBinding.mlLoadingIndicator.loadIndicatorBackground.setVisibility(View.INVISIBLE);
        mBinding.mlLoadingIndicator.loadIndicator.setVisibility(View.INVISIBLE);
        actMovie = data;
        if (data == null) {
            mBinding.mlErrorPage.actionErrorTryAgain.setVisibility(View.VISIBLE);
            mBinding.mlErrorPage.labelErrorNoConnection.setVisibility(View.VISIBLE);
        } else {
            mBinding.mlErrorPage.actionErrorTryAgain.setVisibility(View.INVISIBLE);
            mBinding.mlErrorPage.labelErrorNoConnection.setVisibility(View.INVISIBLE);
            setMovieData(actMovie);
            setPageMode(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {

    }

    void setPageMode(final int viewMode) {
        mBinding.mdHeader.actionFav.setVisibility(viewMode);
        mBinding.mdHeader.filmBackground.setVisibility(viewMode);
        mBinding.mdHeader.filmTitle.setVisibility(viewMode);
        mBinding.mdHeader.releaseDate.setVisibility(viewMode);
        mBinding.divider1.setVisibility(viewMode);
        mBinding.mdDescription.filmDescription.setVisibility(viewMode);
        mBinding.mdDescription.labelFilmDescription.setVisibility(viewMode);
        mBinding.mdMetaData.backgroundHeader.setVisibility(viewMode);
        mBinding.mdMetaData.filmPoster.setVisibility(viewMode);
        mBinding.mdMetaData.filmTagLine.setVisibility(viewMode);
        mBinding.mdMetaData.labelPlayTime.setVisibility(viewMode);
        mBinding.mdMetaData.labelVote.setVisibility(viewMode);
        mBinding.mdMetaData.playTime.setVisibility(viewMode);
        mBinding.mdMetaData.vote.setVisibility(viewMode);
        mBinding.mdTrailers.trailerLabel.setVisibility(viewMode);
        mBinding.mdTrailers.rvMdTrailerList.setVisibility(viewMode);
        mBinding.mdReviews.labelReview.setVisibility(viewMode);
        mBinding.mdReviews.rvMdReviewList.setVisibility(viewMode);
    }

}
