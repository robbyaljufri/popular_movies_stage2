package com.example.android.popular_movies_stage2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

  public static final String CONTENT_AUTHORITY =
      "com.example.android.popular_movies_stage2";

  public static final Uri BASE_CONTENT_URI =
      Uri.parse("content://" + CONTENT_AUTHORITY);

  public static final String PATH_MOVIES   = "movies";
  public static final String PATH_TRAILERS = "trailers";
  public static final String PATH_REVIEWS  = "reviews";

  public static final class MovieEntry implements BaseColumns {

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

    public static final String CONTENT_TYPE =
        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
            + PATH_MOVIES;

    public static final String CONTENT_ITEM_TYPE =
        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
            PATH_MOVIES;

    // table name
    public static final String TABLE_NAME = "movies";

    // columns
    public static final String COLUMN_ID            = "_id";
    public static final String COLUMN_IS_FAVORITE   = "favorite";
    public static final String COLUMN_MOVIE_ID      = "movie_id";
    public static final String COLUMN_OVERVIEW      = "overview";
    public static final String COLUMN_POPULARITY    = "popularity";
    public static final String COLUMN_POSTER_PATH   = "poster_path";
    public static final String COLUMN_RATING        = "rating";
    public static final String COLUMN_RELEASE_DATE  = "release_date";
    public static final String COLUMN_SOURCE        = "source";
    public static final String COLUMN_TITLE         = "title";

    public static Uri buildMovieUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

  }

  public static final class TrailerEntry implements BaseColumns {

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

    public static final String CONTENT_TYPE =
        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
            + PATH_TRAILERS;

    public static final String CONTENT_ITEM_TYPE =
        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
            PATH_TRAILERS;

    public static final String TABLE_NAME = "trailers";

    public static final String COLUMN_ID          = "_id";
    public static final String COLUMN_KEY         = "key";
    public static final String COLUMN_NAME        = "name";
    public static final String COLUMN_MOVIE_ID    = "movie_id";
    public static final String COLUMN_TRAILER_ID  = "trailer_id";

    public static Uri buildTrailerUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

  }

  public static final class ReviewEntry implements BaseColumns {

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

    public static final String CONTENT_TYPE =
        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
            + PATH_REVIEWS;

    public static final String CONTENT_ITEM_TYPE =
        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
            PATH_REVIEWS;

    public static final String TABLE_NAME = "reviews";

    public static final String COLUMN_ID        = "_id";
    public static final String COLUMN_AUTHOR    = "author";
    public static final String COLUMN_CONTENT   = "content";
    public static final String COLUMN_URL       = "url";
    public static final String COLUMN_MOVIE_ID  = "movie_id";
    public static final String COLUMN_REVIEW_ID = "review_id";

    public static Uri buildReviewUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

  }

}
