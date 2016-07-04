package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created by firej on 6/28/2016.
 */
public class DatabaseMaker extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseMaker(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE IF NOT EXISTS " + favoriteMovies.TABLE_NAME + " (" +
                favoriteMovies._ID + " INTEGER PRIMARY KEY," +
                favoriteMovies.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                favoriteMovies.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                favoriteMovies.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                favoriteMovies.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                favoriteMovies.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                favoriteMovies.COLUMN_VOTE_AVERAGE + " REAL NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + favoriteMovies.TABLE_NAME);
        onCreate(db);
    }

    public static final class favoriteMovies implements BaseColumns {

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

    }

    public ArrayList<Movies> retrieveFavoriteMovies() {

        ArrayList<Movies> favorites = new ArrayList<>();

        String query = "SELECT * FROM " + favoriteMovies.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Movies movies = null;
        if (cursor.moveToFirst()) {
            do {
                movies = new Movies();
                movies.setId(cursor.getString(4));
                movies.setTitle(cursor.getString(5));
                movies.setPosterUrl(cursor.getString(1));
                movies.setDate(cursor.getString(3));
                movies.setRatings(cursor.getString(6));
                movies.setOverview(cursor.getString(2));
                favorites.add(movies);
            } while (cursor.moveToNext());
        }

        db.close();

        //Log.v("LOG_TAG", "DATABASEMAKER mMovies: " + )

        return favorites;
    }

    public boolean isFavorite(String mId) {

        boolean isFavorite = false;

        String query = "SELECT * FROM " + favoriteMovies.TABLE_NAME + " WHERE " +
                favoriteMovies.COLUMN_MOVIE_ID + " = " + "'" + mId + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null){
            cursor.moveToFirst();
            if (cursor.getCount() > 0){
                isFavorite = true;
            } else {
                isFavorite = false;
            }
        }

        db.close();
        return isFavorite;
    }

    public void addFavorite(String mPath, String mOverview, String mDate, String mId, String mTitle, String mRating) {

        ContentValues values = new ContentValues();
        values.put(favoriteMovies.COLUMN_POSTER_PATH, mPath);
        values.put(favoriteMovies.COLUMN_OVERVIEW, mOverview);
        values.put(favoriteMovies.COLUMN_RELEASE_DATE, mDate);
        values.put(favoriteMovies.COLUMN_MOVIE_ID, mId);
        values.put(favoriteMovies.COLUMN_ORIGINAL_TITLE, mTitle);
        values.put(favoriteMovies.COLUMN_VOTE_AVERAGE, mRating);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(favoriteMovies.TABLE_NAME, null, values);
    }

    public void deleteFavorite(String movieId) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(favoriteMovies.TABLE_NAME, favoriteMovies.COLUMN_MOVIE_ID + " = " + "'" + movieId + "'", null);
        db.close();
    }

    public void deleteAllFavorites() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ favoriteMovies.TABLE_NAME);
        db.execSQL("VACUUM");
        db.close();
    }
}
