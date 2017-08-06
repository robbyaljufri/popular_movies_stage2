package com.example.android.popular_movies_stage2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.popular_movies_stage2.MovieDetailFragment;
import com.example.android.popular_movies_stage2.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReviewAdapter extends CursorAdapter {

  @Bind(R.id.review_author) TextView mReviewAuthor;
  @Bind(R.id.review_content) TextView mReviewContent;

  public ReviewAdapter(Context context, Cursor c, int flags) {
    super(context, c, flags);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View reviewView = inflater.inflate(R.layout.review, parent, false);
    ButterKnife.bind(this, reviewView);
    return reviewView;
  }

  @Override
  public void bindView(View view, Context context, Cursor data) {

    mReviewAuthor.setText(
        data.getString(MovieDetailFragment.COLUMN_REVIEW_AUTHOR)
    );

    mReviewContent.setText(
        data.getString(MovieDetailFragment.COLUMN_REVIEW_CONTENT)
    );

  }

}
