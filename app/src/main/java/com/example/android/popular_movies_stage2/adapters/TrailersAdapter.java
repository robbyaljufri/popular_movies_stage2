package com.example.android.popular_movies_stage2.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popular_movies_stage2.MovieDetailFragment;
import com.example.android.popular_movies_stage2.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TrailersAdapter extends CursorAdapter {

  @Bind(R.id.trailer_title) TextView mTrailerTitle;

  public TrailersAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View trailerView = inflater.inflate(R.layout.trailer, parent, false);
    ButterKnife.bind(this, trailerView);
    return trailerView;
  }

  @Override
  public void bindView(View view, final Context context, final Cursor cursor) {

    mTrailerTitle.setText(cursor.getString(MovieDetailFragment.COL_TRAILER_NAME));

    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String youtubeKey = cursor.getString(MovieDetailFragment.COL_TRAILER_KEY);
        Intent intent = new Intent(
            Intent.ACTION_VIEW,
            Uri.parse("vnd.youtube:" + youtubeKey)
        );

        if (intent.resolveActivity(context.getPackageManager()) != null) {
          context.startActivity(intent);
        } else {
          Toast.makeText(
            context,
            "No application configured to handle video.",
            Toast.LENGTH_SHORT).show();
        }
      }
    });
  }
}
