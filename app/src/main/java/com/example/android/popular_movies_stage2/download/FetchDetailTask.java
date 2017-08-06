package com.example.android.popular_movies_stage2.download;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popular_movies_stage2.R;
import com.example.android.popular_movies_stage2.panggil.APICallback;
import com.example.android.popular_movies_stage2.data.MovieContract;
import com.example.android.popular_movies_stage2.model.Review;
import com.example.android.popular_movies_stage2.model.Trailer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.util.Vector;


public class FetchDetailTask extends AsyncTask<String, Void, Void> {

  private final String LOG_TAG = FetchDetailTask.class.getSimpleName();

  private final Context mContext;
  private final APICallback mCallback;

  public FetchDetailTask(Context context, APICallback callback) {
    mContext = context;
    mCallback = callback;
  }

  protected Void doInBackground(String... params) {

    Object[] details;

    String detailJSON = null;

    String movieID = params[0];
    String detail  = params[1];

    try {

      final String MOVIE_DB_URL
          = "http://api.themoviedb.org/3/movie/%s/" + detail;

      final String API_KEY_PARAM = "api_key";

      OkHttpClient client = new OkHttpClient();

      HttpUrl movieDbUrl = HttpUrl.parse(String.format(MOVIE_DB_URL, movieID))
          .newBuilder()
          .addQueryParameter(API_KEY_PARAM, mContext.getString(R.string.api_key))
          .build();

      // Log the URL
      Log.v(LOG_TAG, movieDbUrl.toString());

      Request request = new Request.Builder()
          .url(movieDbUrl)
          .build();

      Response response = client.newCall(request).execute();
      detailJSON = response.body().string();
      details = parseJSON(detailJSON, detail);
      addDetails(movieID, detail, details);

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

  private Object[] parseJSON(String json, String detail)
      throws JSONException {
    Object[] parsedDetails;

    final String RESULTS = "results";

    Gson gson = new Gson();
    JsonParser parser = new JsonParser();
    JsonObject object = parser.parse(json).getAsJsonObject();
    if (detail.equals(mContext.getString(R.string.videos))) {
      parsedDetails = gson.fromJson(object.get(RESULTS), Trailer[].class);
    } else {
      parsedDetails = gson.fromJson(object.get(RESULTS), Review[].class);
    }

    return parsedDetails;
  }

  private ContentValues trailerToContentValues(Trailer trailer, String movieID) {

    ContentValues toReturn = new ContentValues();
    toReturn.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieID );
    toReturn.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, trailer.getId());
    toReturn.put(MovieContract.TrailerEntry.COLUMN_KEY, trailer.getKey());
    toReturn.put(MovieContract.TrailerEntry.COLUMN_NAME, trailer.getName());

    return toReturn;
  }

  private ContentValues reviewToContentValues(Review review, String movieID) {

    ContentValues toReturn = new ContentValues();
    toReturn.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieID);
    toReturn.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review.getId());
    toReturn.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
    toReturn.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
    toReturn.put(MovieContract.ReviewEntry.COLUMN_URL, review.getUrl());

    Log.d(LOG_TAG, toReturn.toString());

    return toReturn;
  }

  private int addDetails(String movieID, String detail, Object[] details) {

    int numInserted = 0;

    if (details != null && details.length > 0) {

      Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(details.length);

      for (int i = 0; i < details.length; i++) {

        ContentValues detailValues;
        if (detail.equals(mContext.getString(R.string.videos))) {
          detailValues = trailerToContentValues((Trailer) details[i], movieID);
        } else {
          detailValues = reviewToContentValues((Review) details[i], movieID);
        }
        contentValuesVector.add(detailValues);
      }

      ContentValues[] contentValuesArray
          = new ContentValues[contentValuesVector.size()];

      contentValuesVector.toArray(contentValuesArray);

      if (detail.equals(mContext.getString(R.string.videos))) {
        numInserted = mContext.getContentResolver()
            .bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, contentValuesArray);
      } else {
        numInserted = mContext.getContentResolver()
            .bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, contentValuesArray);
      }
    }
    return numInserted;
  }
}

