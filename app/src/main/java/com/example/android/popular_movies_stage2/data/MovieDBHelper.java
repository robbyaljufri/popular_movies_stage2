package com.example.android.popular_movies_stage2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MovieDBHelper extends SQLiteOpenHelper {

  public static final String LOG_TAG = MovieDBHelper.class.getSimpleName();

  private static final String DATABASE_NAME = "movies.db";
  private static final int DATABASE_VERSION = 4;


  public MovieDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {

    final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
        MovieContract.MovieEntry.TABLE_NAME + " (" +
        MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        MovieContract.MovieEntry.COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0, " +
        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
        MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
        MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
        MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
        MovieContract.MovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
        MovieContract.MovieEntry.COLUMN_SOURCE + " TEXT, " +
        MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +

        " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
        ") ON CONFLICT IGNORE);";

    Log.d(LOG_TAG, SQL_CREATE_MOVIE_TABLE);

    sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " +
        MovieContract.TrailerEntry.TABLE_NAME + " ( " +
        MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        MovieContract.TrailerEntry.COLUMN_NAME + " TEXT NOT NULL, " +
        MovieContract.TrailerEntry.COLUMN_KEY + " TEXT NOT NULL, " +
        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
        MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +

        " FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_ID +
        ") REFERENCES " + MovieContract.MovieEntry.TABLE_NAME + " (" +
        MovieContract.MovieEntry.COLUMN_MOVIE_ID + ")," +

        "UNIQUE (" + MovieContract.TrailerEntry.COLUMN_TRAILER_ID +
        ") ON CONFLICT REPLACE);";

    Log.d(LOG_TAG, SQL_CREATE_TRAILERS_TABLE);

    sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);

    final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " +
        MovieContract.ReviewEntry.TABLE_NAME + " ( " +
        MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
        MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
        MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
        MovieContract.ReviewEntry.COLUMN_URL + " TEXT, " +
        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
        MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +

        " FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIE_ID +
        ") REFERENCES " + MovieContract.MovieEntry.TABLE_NAME + " (" +
        MovieContract.MovieEntry.COLUMN_MOVIE_ID + "), " +

        "UNIQUE (" + MovieContract.ReviewEntry.COLUMN_REVIEW_ID +
        ") ON CONFLICT REPLACE);";

    Log.d(LOG_TAG, SQL_CREATE_REVIEWS_TABLE);

    sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion,
                        int newVersion) {

    Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
        newVersion + ". OLD DATA WILL BE DESTROYED");

    // Drop the Movies table
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "
        + MovieContract.MovieEntry.TABLE_NAME);

    // Drop the Movies ID Sequence
    sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
        MovieContract.MovieEntry.TABLE_NAME + "'");

    // Drop the Trailers table
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "
        + MovieContract.TrailerEntry.TABLE_NAME);

    // Drop the Trailers ID Sequence
    sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
        MovieContract.TrailerEntry.TABLE_NAME + "'");

    // Drop the Reviews table
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "
        + MovieContract.ReviewEntry.TABLE_NAME);

    // Drop the Reviews ID Sequence
    sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
        MovieContract.ReviewEntry.TABLE_NAME + "'");

    onCreate(sqLiteDatabase);

  }

}
