package com.example.android.popular_movies_stage2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class MovieProvider extends ContentProvider {

  private static final String LOG_TAG = MovieProvider.class.getSimpleName();

  private static final UriMatcher sUriMatcher = buildUriMatcher();
  private MovieDBHelper mMovieDBHelper;

  // Codes for the UriMatcher
  private static final int MOVIE = 100;
  private static final int MOVIE_WITH_ID = 101;
  private static final int TRAILER = 200;
  private static final int TRAILER_WITH_MOVIE_ID = 201;
  private static final int REVIEW = 300;
  private static final int REVIEW_WITH_MOVIE_ID = 301;


  private static UriMatcher buildUriMatcher() {

    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = MovieContract.CONTENT_AUTHORITY;

    // Movies
    matcher.addURI(authority, MovieContract.MovieEntry.TABLE_NAME, MOVIE);

    matcher.addURI(
        authority,
        MovieContract.MovieEntry.TABLE_NAME + "/#",
        MOVIE_WITH_ID
    );

    // Trailers
    matcher.addURI(authority, MovieContract.TrailerEntry.TABLE_NAME, TRAILER);

    matcher.addURI(
        authority,
        MovieContract.TrailerEntry.TABLE_NAME + "/#",
        TRAILER_WITH_MOVIE_ID
    );

    // Reviews
    matcher.addURI(authority, MovieContract.ReviewEntry.TABLE_NAME, REVIEW);

    matcher.addURI(
        authority,
        MovieContract.ReviewEntry.TABLE_NAME + "/#",
        REVIEW_WITH_MOVIE_ID
    );

    return matcher;
  }

  private Cursor getMovie (String[] projection, String selection,
                              String[] selectionArgs, String sortOrder) {


    return mMovieDBHelper.getReadableDatabase().query(
        MovieContract.MovieEntry.TABLE_NAME,
        projection,
        selection,
        selectionArgs,
        null, // GROUP BY
        null, // HAVING
        sortOrder
    );
  }

  private Cursor getMovieByID (Uri uri, String[] projection, String sortOrder) {

    String selection = MovieContract.MovieEntry.TABLE_NAME +
        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
    String[] args = new String[] {String.valueOf(ContentUris.parseId(uri))};
    return getMovie(projection, selection, args, sortOrder);
  }

  private Cursor getTrailers(String[] projection, String selection,
                           String[] selectionArgs, String sortOrder) {

    return mMovieDBHelper.getReadableDatabase().query(
        MovieContract.TrailerEntry.TABLE_NAME,
        projection,
        selection,
        selectionArgs,
        null, // GROUP BY
        null, // HAVING
        sortOrder
    );
  }

  private Cursor getTrailersByMovieID (Uri uri, String[] projection,
                                       String sortOrder) {

    String selection = MovieContract.TrailerEntry.TABLE_NAME + "."
        + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?";
    String[] args = new String[] {String.valueOf(ContentUris.parseId(uri))};
    return getTrailers(projection, selection, args, sortOrder);
  }

  private Cursor getReviews(String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {

    return mMovieDBHelper.getReadableDatabase().query(
        MovieContract.ReviewEntry.TABLE_NAME,
        projection,
        selection,
        selectionArgs,
        null, // GROUP BY
        null, // HAVING
        sortOrder
    );
  }

  private Cursor getReviewsByMovieID (Uri uri, String[] projection,
                                       String sortOrder) {

    String selection = MovieContract.ReviewEntry.TABLE_NAME + "."
        + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?";
    String[] args = new String[] {String.valueOf(ContentUris.parseId(uri))};
    return getReviews(projection, selection, args, sortOrder);
  }

  @Override
  public boolean onCreate(){
    mMovieDBHelper = new MovieDBHelper(getContext());
    return true;
  }

  @Override
  public String getType(Uri uri) {
    final int match = sUriMatcher.match(uri);

    switch (match) {
      case MOVIE:
        return MovieContract.MovieEntry.CONTENT_TYPE;

      case MOVIE_WITH_ID:
        return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;

      case TRAILER:
        return MovieContract.TrailerEntry.CONTENT_TYPE;

      case TRAILER_WITH_MOVIE_ID:
        return MovieContract.TrailerEntry.CONTENT_ITEM_TYPE;

      case REVIEW:
        return MovieContract.ReviewEntry.CONTENT_TYPE;

      case REVIEW_WITH_MOVIE_ID:
        return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;

      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
    Cursor results = null;
    switch(sUriMatcher.match(uri)) {

      case MOVIE: {
        results = getMovie(projection, selection, selectionArgs, sortOrder);
        break;
      }

      case MOVIE_WITH_ID: {
        results = getMovieByID(uri, projection, sortOrder);
        break;
      }

      case TRAILER: {
        results = getTrailers(projection, selection, selectionArgs, sortOrder);
        break;
      }

      case TRAILER_WITH_MOVIE_ID: {
        results = getTrailersByMovieID(uri, projection, sortOrder);
        break;
      }

      case REVIEW: {
        results = getReviews(projection, selection, selectionArgs, sortOrder);
        break;
      }

      case REVIEW_WITH_MOVIE_ID: {
        results = getReviewsByMovieID(uri, projection, sortOrder);
        break;
      }

      default:{
        throw new UnsupportedOperationException("Unknown uri: " + uri);
      }

    }
    return results;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    Uri uriToReturn;

    switch (match) {
      case MOVIE: {
        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
        // insert unless it is already contained in the database
        if (_id > 0) {
          uriToReturn = MovieContract.MovieEntry.buildMovieUri(_id);
        } else {
          throw new android.database.SQLException("Failed to insert row into: "
              + uri);
        }
        break;
      }

      case TRAILER: {
        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
        // insert unless it is already contained in the database
        if (_id > 0) {
          uriToReturn = MovieContract.TrailerEntry.buildTrailerUri(_id);
        } else {
          throw new android.database.SQLException("Failed to insert row into: "
              + uri);
        }
        break;
      }

      case REVIEW: {
        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
        // insert unless it is already contained in the database
        if (_id > 0) {
          uriToReturn = MovieContract.ReviewEntry.buildReviewUri(_id);
        } else {
          throw new android.database.SQLException("Failed to insert row into: "
              + uri);
        }
        break;
      }
      default: {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
      }
    }

    getContext().getContentResolver().notifyChange(uri, null);
    return uriToReturn;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {

    final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int rowsDeleted = 0;

    switch (match) {

      case MOVIE: {
        rowsDeleted = db.delete(
            MovieContract.MovieEntry.TABLE_NAME,
            selection,
            selectionArgs
        );
        break;
      }

      case MOVIE_WITH_ID: {

        rowsDeleted = db.delete(
            MovieContract.MovieEntry.TABLE_NAME,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
            new String[]{String.valueOf(ContentUris.parseId(uri))}
        );

        break;
      }

      case TRAILER_WITH_MOVIE_ID: {

        rowsDeleted = db.delete(
            MovieContract.TrailerEntry.TABLE_NAME,
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
            new String[]{String.valueOf(ContentUris.parseId(uri))}
        );

        break;
      }

      case REVIEW_WITH_MOVIE_ID: {

        rowsDeleted = db.delete(
            MovieContract.ReviewEntry.TABLE_NAME,
            MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
            new String[]{String.valueOf(ContentUris.parseId(uri))}
        );

        break;
      }

      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);

    }
    if (rowsDeleted > 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return rowsDeleted;
  }

  @Override
  public int bulkInsert(Uri uri, ContentValues[] values) {

    final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    switch (match) {
      case MOVIE: {
        db.beginTransaction();
        int rowsInserted = 0;

        try {
          for (ContentValues value : values) {
            if (value == null){
              throw new IllegalArgumentException("Null content values not allowed");
            }

            long _id = db.insert(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                value
            );

            if (_id != -1) {
              rowsInserted++;
            }
          }
          if (rowsInserted > 0) {
            db.setTransactionSuccessful();
          }
        } finally {
          db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsInserted;
      }

      case TRAILER: {
        db.beginTransaction();
        int rowsInserted = 0;

        try {
          for (ContentValues value : values) {
            if (value == null){
              throw new IllegalArgumentException("Null content values not allowed");
            }

            long _id = db.insert(
                MovieContract.TrailerEntry.TABLE_NAME,
                null,
                value
            );

            if (_id != -1) {
              rowsInserted++;
            }
          }
          if (rowsInserted > 0) {
            db.setTransactionSuccessful();
          }
        } finally {
          db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsInserted;
      }

      case REVIEW: {
        db.beginTransaction();
        int rowsInserted = 0;

        try {
          for (ContentValues value : values) {
            if (value == null){
              throw new IllegalArgumentException("Null content values not allowed");
            }

            long _id = db.insert(
                MovieContract.ReviewEntry.TABLE_NAME,
                null,
                value
            );

            if (_id != -1) {
              rowsInserted++;
            }
          }
          if (rowsInserted > 0) {
            db.setTransactionSuccessful();
          }
        } finally {
          db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsInserted;
      }


      default:
        return super.bulkInsert(uri, values);
    }

  }

  @Override
  public int update(Uri uri, ContentValues contentValues, String selection,
                    String[] selectionArgs) {
    Log.d(LOG_TAG, "update");
    final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
    int rowsUpdated = 0;

    if (contentValues == null){
      throw new IllegalArgumentException("Null content values not allowed");
    }

    int match = sUriMatcher.match(uri);
    switch(match){

      case MOVIE: {
        rowsUpdated = db.update(
            MovieContract.MovieEntry.TABLE_NAME,
            contentValues,
            selection,
            selectionArgs);
        break;
      }

      case MOVIE_WITH_ID: {
        Log.d(LOG_TAG, "MOVIE_WITH_ID");
        Log.d(LOG_TAG, contentValues.toString());
        rowsUpdated = db.update(
            MovieContract.MovieEntry.TABLE_NAME,
            contentValues,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
            new String[] {String.valueOf(ContentUris.parseId(uri))});
        break;
      }

      case TRAILER_WITH_MOVIE_ID: {
        Log.d(LOG_TAG, "TRAILER_WITH_MOVIE_ID");
        Log.d(LOG_TAG, uri.toString());
        rowsUpdated = db.update(
            MovieContract.TrailerEntry.TABLE_NAME,
            contentValues,
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
            new String[] {String.valueOf(ContentUris.parseId(uri))});
        break;
      }

      case REVIEW_WITH_MOVIE_ID: {
        Log.d(LOG_TAG, "REVIEW_WITH_MOVIE_ID");
        Log.d(LOG_TAG, uri.toString());
        rowsUpdated = db.update(
            MovieContract.ReviewEntry.TABLE_NAME,
            contentValues,
            MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
            new String[] {String.valueOf(ContentUris.parseId(uri))});
        break;
      }

      default:{
        throw new UnsupportedOperationException("Unknown uri: " + uri);
      }
    }

    if (rowsUpdated > 0){
      Log.d(LOG_TAG, uri.toString());
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return rowsUpdated;
  }

}
