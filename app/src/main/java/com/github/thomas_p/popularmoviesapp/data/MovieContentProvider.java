package com.github.thomas_p.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.security.InvalidParameterException;


public class MovieContentProvider extends ContentProvider {


    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_ID = 101;

    public static final int FAVORITES_WITH_ID_AND_TRAILER = 111;
    public static final int FAVORITES_WITH_ID_AND_REVIEWS = 121;


    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MovieDBHelper mMovieDB;


    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.PATH_FAVORITE, FAVORITES);
        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.PATH_FAVORITE + "/#", FAVORITES_WITH_ID);
        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.PATH_FAVORITE + "/#/trailers", FAVORITES_WITH_ID_AND_TRAILER);
        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.PATH_FAVORITE + "/#/reviews", FAVORITES_WITH_ID_AND_REVIEWS);

        // @// DONE: 01.03.2017 add favorites
        //uriMatcher.addURI();
        return uriMatcher;
    }


    /**
     * create Content Provider
     *
     * @return true
     */
    @Override
    public boolean onCreate() {
        final Context context = getContext();
        assert context != null;
        mMovieDB = new MovieDBHelper(context);
        return true;
    }


    /**
     * @param uri           uri for action
     * @param projection    projection for the items
     * @param selection     WHERE string with ? placeholders
     * @param selectionArgs values for placeholders as an array
     * @param sortOrder     sort order
     * @return a Cursor with the results
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mMovieDB.getReadableDatabase();
        String[] movie_id = {null};
        if (uri.getPathSegments().size() > 1){
            movie_id[0] = uri.getPathSegments().get(1);
        }
        switch (match) {
            case FAVORITES:
                return db.query(PopularMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case FAVORITES_WITH_ID:
                return db.query(PopularMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        PopularMoviesContract.FavoriteMovieEntry._ID + "=?",
                        movie_id,
                        null,
                        null,
                        sortOrder);
            case FAVORITES_WITH_ID_AND_TRAILER:
                return db.query(PopularMoviesContract.TrailerEntry.TABLE_NAME,
                        projection,
                        PopularMoviesContract.TrailerEntry.COLUMN_MOVIE_ID + "=?",
                        movie_id,
                        null,
                        null,
                        sortOrder);
            case FAVORITES_WITH_ID_AND_REVIEWS:
                return db.query(PopularMoviesContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        PopularMoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + "=?",
                        movie_id,
                        null,
                        null,
                        sortOrder);
            default:
                throw new UnknownError("Invalid uri");
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
            case FAVORITES_WITH_ID:
                return PopularMoviesContract.FavoriteMovieEntry.class.getName();
            case FAVORITES_WITH_ID_AND_TRAILER:
                return PopularMoviesContract.TrailerEntry.class.getName();
            case FAVORITES_WITH_ID_AND_REVIEWS:
                return PopularMoviesContract.ReviewsEntry.class.getName();
        }
        return null;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mMovieDB.getWritableDatabase();
        final String tableName;
        int movieId = 0;
        switch (match) {
            case FAVORITES:
                tableName = PopularMoviesContract.FavoriteMovieEntry.TABLE_NAME;
                break;
            case FAVORITES_WITH_ID_AND_TRAILER:
                tableName = PopularMoviesContract.TrailerEntry.TABLE_NAME;
                movieId = values.getAsInteger(PopularMoviesContract.TrailerEntry.COLUMN_MOVIE_ID);
                break;
            case FAVORITES_WITH_ID_AND_REVIEWS:
                tableName = PopularMoviesContract.ReviewsEntry.TABLE_NAME;
                movieId = values.getAsInteger(PopularMoviesContract.TrailerEntry.COLUMN_MOVIE_ID);
            default:
                throw new InvalidParameterException("Invalid uri.");
        }
        long id = db.insert(tableName, null, values);
        if (id > 0) {
            // notify change
            final Context context = getContext();
            if (context != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            // return new uri
            switch (match) {
                case FAVORITES:
                    return PopularMoviesContract.FavoriteMovieEntry.getFavoriteMovie((int) id);
                case FAVORITES_WITH_ID_AND_TRAILER:
                    return Uri.withAppendedPath(PopularMoviesContract.FavoriteMovieEntry.getTrailerUri(movieId), Long.toString(id));
                case FAVORITES_WITH_ID_AND_REVIEWS:
                    return Uri.withAppendedPath(PopularMoviesContract.FavoriteMovieEntry.getReviewsUri(movieId), Long.toString(id));
            }
        } else {
            throw new android.database.SQLException("Failed to insert row into " + uri);
        }
        return null;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            // favorites are the only one which can be deleted.
            case FAVORITES_WITH_ID:
                final SQLiteDatabase db = mMovieDB.getWritableDatabase();
                final String[] id = {uri.getPathSegments().get(1)};
                // delete movie
                final int deleteCount = db.delete(
                        PopularMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                        PopularMoviesContract.FavoriteMovieEntry._ID + "=?",
                        id);
                /*
                // deleting the trailers
                db.delete(PopularMoviesContract.TrailerEntry.TABLE_NAME,
                        PopularMoviesContract.TrailerEntry.COLUMN_MOVIE_ID + "=?",
                        id);
                // deleting the reviews
                db.delete(PopularMoviesContract.ReviewsEntry.TABLE_NAME,
                        PopularMoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + "=?",
                        id);
                */
                final Context context = getContext();
                if (deleteCount != 0 && context != null) {
                    // notify changes
                    context.getContentResolver().notifyChange(uri, null);
                }
                return deleteCount;
            default:
                throw new InvalidParameterException("Invalid uri.");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // we do not need it for the actual context
        return 0;
    }
}
