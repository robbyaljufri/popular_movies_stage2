package com.example.android.popular_movies_stage2;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

  public static final String LOG_TAG = Utils.class.getSimpleName();

  public static final String EMPTY = "";

  public static final boolean isStringEmpty(String toTest) {
    return (toTest == null || toTest.equals(EMPTY));
  }

  public static boolean isCursorEmpty(Cursor data) {
    return (data == null || !data.moveToNext());
  }

  public static final String getSortOrder(Context context) {
    SharedPreferences prefs =
        PreferenceManager.getDefaultSharedPreferences(context);

    String sortOrder =  prefs.getString(
        context.getString(R.string.movie_sort_key),
        context.getString(R.string.movie_sort_default)
    );
    Log.d(LOG_TAG, "Sort order: " + sortOrder);
    return sortOrder;
  }

  public static boolean sortOrderChanged(String oldOrder, String newOrder) {
    return (!oldOrder.equals(newOrder));
  }

  public static String getMoviePosterURL (Context context, String path) {

    StringBuffer toReturn = new StringBuffer();
    toReturn.append(context.getString(R.string.movie_poster_url));
    String size = context.getString(R.string.movie_poster_size);

    if (path != null) {
      toReturn.append(size + path);
    }

    return toReturn.toString();
  }

  public static String getFormattedReleaseDate (Context context,
                                                String releaseDate) {
    String toReturn = "N/A";
    if (!Utils.isStringEmpty(releaseDate)) {

      SimpleDateFormat dateFormat = new SimpleDateFormat(
          context.getString(R.string.movie_date_db_format),
          Locale.US
      );

      try {
        Date parsed = dateFormat.parse(releaseDate);
        System.out.println(parsed);
        dateFormat = new SimpleDateFormat(
            context.getString(R.string.movie_date_display_format),
            Locale.US
        );
        toReturn = dateFormat.format(parsed);
      } catch (ParseException pException) {
        Log.e(LOG_TAG, "Error parsing date: " + pException.toString());
      }
    }
    return toReturn;
  }

}
