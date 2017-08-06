package com.example.android.popular_movies_stage2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popular_movies_stage2.MovieListActivityFragment;
import com.example.android.popular_movies_stage2.R;
import com.example.android.popular_movies_stage2.Utils;
import com.squareup.picasso.Picasso;

public class MovieListAdapter extends CursorAdapter {

  private final String LOG_TAG = MovieListAdapter.class.getSimpleName();

  public MovieListAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {

    LayoutInflater inflater = LayoutInflater.from(context);
    View detailView = inflater.inflate(R.layout.movie_poster, parent, false);
    return detailView;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {

    ImageView poster = (ImageView) view;
    String posterUrl = Utils.getMoviePosterURL(
        context,
        cursor.getString(MovieListActivityFragment.COL_POSTER_PATH)
    );

    Picasso.with(context)
        .load(posterUrl)
        .error(R.drawable.no_poster_available)
        .into(poster);
  }
}
