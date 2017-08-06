package com.example.android.popular_movies_stage2.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popular_movies_stage2.R;
import com.example.android.popular_movies_stage2.panggil.APICallback;
import com.example.android.popular_movies_stage2.data.MovieContract.MovieEntry;
import com.example.android.popular_movies_stage2.model.Movie;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Vector;

public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

  private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

  private final Context mContext;
  private final APICallback mCallback;

  public FetchMoviesTask(Context context, APICallback callback) {
    mContext = context;
    mCallback = callback;
  }

  protected Void doInBackground(String... params) {

    Movie[] movies;

    String movieJSON;
    String sortOrder = params[0];

    try {

      final String MOVIE_DB_URL = "http://api.themoviedb.org/3/movie/popular";
      final String SORT_PARAM = "sort_by";
      final String API_KEY_PARAM = "api_key";
      final String PAGE_PARAM = "page";
      final String VOTE_COUNT_PARAM = "vote_count.gte";

      OkHttpClient client = new OkHttpClient();

      HttpUrl movieDbUrl = HttpUrl.parse(MOVIE_DB_URL)
          .newBuilder()
          .addQueryParameter(SORT_PARAM, sortOrder)
          .addQueryParameter(PAGE_PARAM, "1")
          .addQueryParameter(API_KEY_PARAM, mContext.getString(R.string.api_key))
          .addQueryParameter(VOTE_COUNT_PARAM, mContext.getString(R.string.vote_count_threshold))
          .build();

      Log.v(LOG_TAG, movieDbUrl.toString());

      Request request = new Request.Builder()
          .url(movieDbUrl)
          .build();

      Response response = client.newCall(request).execute();
      movieJSON = response.body().string();
      movies = parseMovieJSON(movieJSON);
      addMovies(movies, sortOrder);

    } catch (IOException ioException) {
      Log.e(LOG_TAG, "IO Error ", ioException);
    } catch (JSONException jsonException) {
      Log.e(LOG_TAG, "Error parsing JSON: ", jsonException);
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void result) {
    mCallback.onCallCompleted();
  }

  private Movie[] parseMovieJSON(String movieJSONString)
      throws JSONException {

    final String RESULTS = "results";
    Gson gson = new Gson();
    JsonParser parser = new JsonParser();
    JsonObject object = parser.parse(movieJSONString).getAsJsonObject();
    Movie[] movies = gson.fromJson(object.get(RESULTS), Movie[].class);
    return movies;
  }

  private ContentValues movieToValues(Movie movie) {
    ContentValues toReturn = new ContentValues();

    toReturn.put(MovieEntry.COLUMN_MOVIE_ID, movie.getId());
    toReturn.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
    toReturn.put(MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
    toReturn.put(MovieEntry.COLUMN_RATING, movie.getRating());
    toReturn.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
    toReturn.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
    toReturn.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());

    return toReturn;
  }

  private int deleteNonFavorites() {
    String selector = MovieEntry.COLUMN_IS_FAVORITE + " = ?";
    String[] args = new String[] {"0"};
    int numDeleted = mContext.getContentResolver()
        .delete(MovieEntry.CONTENT_URI, selector, args);
    return numDeleted;
  }

  private int updateFavoriteSource(HashSet<Long> movieIDs, String sortOrder) {
    int numUpdated = 0;

    Cursor faves = mContext.getContentResolver().query(
        MovieEntry.CONTENT_URI,
        new String[] {MovieEntry.COLUMN_MOVIE_ID},
        MovieEntry.COLUMN_IS_FAVORITE + " =  ? ",
        new String[]{"1"},
        null
    );

    while (faves.moveToNext()) {
      Long movieID = faves.getLong(0);
      if (movieIDs.contains(movieID)) {
        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_SOURCE, sortOrder);
        numUpdated = mContext.getContentResolver().update(
            MovieEntry.CONTENT_URI,
            values,
            MovieEntry.COLUMN_MOVIE_ID + " = ? ",
            new String[] {movieID.toString()}
        );
      }
    }
    return numUpdated;
  }

  private int addMovies(Movie[] movies, String sortOrder) {

    int numInserted = 0;
    HashSet<Long> movieIDs = new HashSet<Long>();

    if (movies.length > 0) {
      Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(movies.length);
      for (int i = 0; i < movies.length; i++) {

        ContentValues movieValues = movieToValues(movies[i]);
        movieValues.put(MovieEntry.COLUMN_SOURCE, sortOrder);
        Log.d(LOG_TAG, movieValues.toString());
        contentValuesVector.add(movieValues);
        movieIDs.add(new Long(movies[i].getId()));

      }

      ContentValues[] contentValuesArray
          = new ContentValues[contentValuesVector.size()];

      contentValuesVector.toArray(contentValuesArray);

      int numDeleted = deleteNonFavorites();

      int numUpdated = updateFavoriteSource(movieIDs, sortOrder);

      numInserted = mContext.getContentResolver()
          .bulkInsert(MovieEntry.CONTENT_URI, contentValuesArray);
    }
    return numInserted;
  }

}
