package com.github.thomas_p.popularmoviesapp.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class PopularMoviesContract {

    public static final String AUTHORITY = "com.github.thomas_p.popularmoviesapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITE = "favorites";

    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_REVIEWS = "reviews";


    public static class FavoriteMovieEntry implements BaseColumns {
        // content uri path
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_FAVORITE)
                .build();

        public static Uri getFavoriteMovie(int movieId) {
            return CONTENT_URI
                    .buildUpon()
                    .appendPath(String.valueOf(movieId))
                    .build();
        }

        public static Uri getTrailerUri(int movieId) {
            return getFavoriteMovie(movieId)
                    .buildUpon()
                    .appendPath(PATH_TRAILERS)
                    .build();
        };

        public static Uri getReviewsUri(int movieId) {
            return getFavoriteMovie(movieId)
                    .buildUpon()
                    .appendPath(PATH_REVIEWS)
                    .build();
        }
        //
        // table name
        //
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_TAGLINE = "tagline";
        public static final String COLUMN_RELEASE_DATE = "release";
        public static final String COLUMN_VOTE_AVERAGE = "votes";
        public static final String COLUMN_BUDGET = "budget";
        public static final String COLUMN_REVENUE = "revenue";
        public static final String COLUMN_RUNTIME = "runtime";

        public static final String COLUMN_HOMEPAGE = "homepage";

        public static final String[] PROJECTION = {
                _ID,
                COLUMN_TITLE,
                COLUMN_TAGLINE,
                COLUMN_OVERVIEW,
                COLUMN_RELEASE_DATE,
                COLUMN_VOTE_AVERAGE,
                COLUMN_BACKDROP_PATH,
                COLUMN_POSTER_PATH,
                COLUMN_BUDGET,
                COLUMN_REVENUE,
                COLUMN_RUNTIME,
                COLUMN_HOMEPAGE
        };
    }


    public static final class TrailerEntry implements BaseColumns {
        public static final String TABLE_NAME = "trailers";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ = "";
    }

    public static final class ReviewsEntry implements BaseColumns {
        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_MOVIE_ID = "movie_id";

    }

}
