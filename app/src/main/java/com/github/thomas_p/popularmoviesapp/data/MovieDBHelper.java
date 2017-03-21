package com.github.thomas_p.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.github.thomas_p.popularmoviesapp.data.PopularMoviesContract.*;



public class MovieDBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "PopularMovie.db";
    private static final String[] TABLES = {
            FavoriteMovieEntry.TABLE_NAME,
            TrailerEntry.TABLE_NAME,
            ReviewsEntry.TABLE_NAME
    };

    public MovieDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_DB = "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME + " (" +
                FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY UNIQUE ON CONFLICT IGNORE, " +
                FavoriteMovieEntry.COLUMN_TITLE + " TEXT, " +
                FavoriteMovieEntry.COLUMN_TAGLINE + " TEXT, " +
                FavoriteMovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                FavoriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                FavoriteMovieEntry.COLUMN_VOTE_AVERAGE + " REAL, " +
                FavoriteMovieEntry.COLUMN_BACKDROP_PATH + " TEXT, " +
                FavoriteMovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                FavoriteMovieEntry.COLUMN_BUDGET + " INTEGER, " +
                FavoriteMovieEntry.COLUMN_REVENUE + " INTEGER, " +
                FavoriteMovieEntry.COLUMN_RUNTIME + " INTEGER, " +
                FavoriteMovieEntry.COLUMN_HOMEPAGE + " TEXT" +
                ")";
        // @// TODO: 02.03.2017 add trailer and review


        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String TABLE : TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        }
        onCreate(db);
    }
}
