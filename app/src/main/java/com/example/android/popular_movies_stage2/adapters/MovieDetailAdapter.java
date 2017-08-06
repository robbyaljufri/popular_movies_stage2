package com.example.android.popular_movies_stage2.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popular_movies_stage2.MovieDetailFragment;
import com.example.android.popular_movies_stage2.R;
import com.example.android.popular_movies_stage2.Utils;
import com.example.android.popular_movies_stage2.data.MovieContract;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailAdapter extends CursorAdapter {

  public static final String LOG_TAG = MovieDetailAdapter.class.getSimpleName();
  public static final String FAVORITES_UPDATED = "FavoritesUpdated";


  @Bind(R.id.movie_detail_title) TextView mMovieTitle;
  @Bind(R.id.movie_detail_overview) TextView mMovieOverview;
  @Bind(R.id.movie_detail_rating) TextView mMovieRating;
  @Bind(R.id.movie_detail_release_date) TextView mMovieReleaseDate;
  @Bind(R.id.movie_detail_poster) ImageView mMoviePoster;
  @Bind(R.id.favorite) CheckBox mFavorite;

  private long mMovieID = 0;

  public MovieDetailAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
  }

  @Override
  public View newView(Context context, Cursor data, ViewGroup parent) {
    Log.d(LOG_TAG, DatabaseUtils.dumpCursorToString(data));

    LayoutInflater inflater = LayoutInflater.from(context);
    View detailView = inflater.inflate(R.layout.movie_detail, parent, false);
    ButterKnife.bind(this, detailView);
    return detailView;
  }

  @Override
  public void bindView(View view, final Context context, final Cursor data) {

    Log.d(LOG_TAG, DatabaseUtils.dumpCursorToString(data));

    mMovieID = data.getLong(MovieDetailFragment.COL_MOVIE_ID);

    mMovieTitle.setText(data.getString(MovieDetailFragment.COL_MOVIE_TITLE));

    mMovieOverview.setText(data.getString(MovieDetailFragment.COL_MOVIE_OVERVIEW));

    float movieRating = data.getFloat(MovieDetailFragment.COL_MOVIE_RATING);
    mMovieRating.setText(context.getString(R.string.user_rating, movieRating));

    String releaseDate = data.getString(MovieDetailFragment.COL_MOVIE_RELEASE_DATE);
    mMovieReleaseDate.setText(
        Utils.getFormattedReleaseDate(context, releaseDate)
    );

    int isFavorite = data.getInt(MovieDetailFragment.COL_MOVIE_IS_FAVORITE);
    if (isFavorite == 1) {
      mFavorite.setChecked(true);
      //int color = ContextCompat.getColor(context, R.color.black);
      //mFavorite.setTextColor(color);
    }

    String posterPath = data.getString(MovieDetailFragment.COL_MOVIE_POSTER_PATH);

    Picasso.with(context)
        .load(Utils.getMoviePosterURL(context, posterPath))
        .error(R.drawable.no_poster_available)
        .into(mMoviePoster);
  }

  private void favoriteChanged(Context context) {

    SharedPreferences prefs  =
        PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putBoolean(FAVORITES_UPDATED, true);
    editor.commit();

  }

  @OnClick(R.id.favorite)
  public void saveFavorite(View view) {

    Context context = view.getContext();

    favoriteChanged(context);

    CheckBox checkBox = (CheckBox) view;

    int favoriteValue = (checkBox.isChecked() ? 1 : 0);

    ContentValues toReturn = new ContentValues();
    toReturn.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, favoriteValue);

    Uri mMovieURI = MovieContract.MovieEntry.buildMovieUri(mMovieID);

    int updated = context.getContentResolver().update(
        mMovieURI,
        toReturn,
        null,
        null
    );

    /*
    int color = 0;
    if (checkBox.isChecked()) {
      color = ContextCompat.getColor(context, R.color.black);
    } else {
      color = ContextCompat.getColor(context, R.color.light_gray);
    }
    ((CheckBox) view).setTextColor(color);
    */
    Toast toast = Toast.makeText(
        context,
        context.getString(R.string.updating_favorites),
        Toast.LENGTH_SHORT
    );
    toast.show();
  }
}
