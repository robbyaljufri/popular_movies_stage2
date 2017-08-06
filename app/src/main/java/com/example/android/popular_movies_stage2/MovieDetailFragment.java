package com.example.android.popular_movies_stage2;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.commonsware.cwac.merge.MergeAdapter;
import com.example.android.popular_movies_stage2.adapter.AdapterMovieDetail;
import com.example.android.popular_movies_stage2.adapter.AdapterReviews;
import com.example.android.popular_movies_stage2.adapter.AdapterTrailers;
import com.example.android.popular_movies_stage2.panggil.APICallback;
import com.example.android.popular_movies_stage2.data.MovieContract;
import com.example.android.popular_movies_stage2.download.FetchDetailTask;

public class MovieDetailFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

  public static final String MOVIE_URI = "MOVIE_URI";
  public static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

  private static final int MOVIE_LOADER_ID = 1;
  private static final int TRAILER_LOADER_ID = 2;
  private static final int REVIEW_LOADER_ID = 3;

  private Uri mMovieURI;
  private AdapterMovieDetail mAdapterMovieDetail;
  private AdapterTrailers mTrailerAdapter;
  private boolean mTrailersLoaded;
  private AdapterReviews mAdapterReviews;
  private boolean mReviewsLoaded;
  private MergeAdapter mMergeAdapter;

  private static final String[] MOVIE_COLUMNS = {
      MovieContract.MovieEntry.COLUMN_ID,
      MovieContract.MovieEntry.COLUMN_MOVIE_ID,
      MovieContract.MovieEntry.COLUMN_TITLE,
      MovieContract.MovieEntry.COLUMN_OVERVIEW,
      MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
      MovieContract.MovieEntry.COLUMN_POPULARITY,
      MovieContract.MovieEntry.COLUMN_RATING,
      MovieContract.MovieEntry.COLUMN_POSTER_PATH,
      MovieContract.MovieEntry.COLUMN_IS_FAVORITE
  };

  public static final int COL_ID = 0;
  public static final int COL_MOVIE_ID = 1;
  public static final int COL_MOVIE_TITLE = 2;
  public static final int COL_MOVIE_OVERVIEW = 3;
  public static final int COL_MOVIE_RELEASE_DATE = 4;
  public static final int COL_MOVIE_POPULARITY = 5;
  public static final int COL_MOVIE_RATING = 6;
  public static final int COL_MOVIE_POSTER_PATH = 7;
  public static final int COL_MOVIE_IS_FAVORITE = 8;

  private static final String[] TRAILER_COLUMNS = {
      MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
      MovieContract.TrailerEntry.COLUMN_KEY,
      MovieContract.TrailerEntry.COLUMN_TRAILER_ID,
      MovieContract.TrailerEntry.COLUMN_NAME,
      MovieContract.TrailerEntry.COLUMN_ID,
  };

  public static final int COL_TRAILER_MOVIE_ID = 0;
  public static final int COL_TRAILER_KEY = 1;
  public static final int COL_TRAILER_TRAILER_ID = 2;
  public static final int COL_TRAILER_NAME = 3;
  public static final int COL_TRAILER_ID = 4;

  private static final String[] REVIEW_COLUMNS = {
      MovieContract.ReviewEntry.COLUMN_MOVIE_ID,
      MovieContract.ReviewEntry.COLUMN_AUTHOR,
      MovieContract.ReviewEntry.COLUMN_CONTENT,
      MovieContract.ReviewEntry.COLUMN_URL,
      MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
      MovieContract.ReviewEntry.COLUMN_ID,
  };

  public static final int COLUMN_REVIEW_MOVIE_ID = 0;
  public static final int COLUMN_REVIEW_AUTHOR = 1;
  public static final int COLUMN_REVIEW_CONTENT = 2;
  public static final int COLUMN_REVIEW_URL = 3;
  public static final int COLUMN_REVIEW_ID = 4;

  public MovieDetailFragment() {
    setHasOptionsMenu(true);
  }

  private CursorLoader getCursorLoader(Uri uri, String[] columns) {
    return new CursorLoader(
        getActivity(),
        uri,
        columns,
        null,
        null,
        null
    );
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {

    if (mMovieURI != null) {
      long movieID = ContentUris.parseId(mMovieURI);
      switch (id) {
        case MOVIE_LOADER_ID: {
          Log.v(LOG_TAG, mMovieURI.toString());
          return getCursorLoader(mMovieURI, MOVIE_COLUMNS);
        }

        case TRAILER_LOADER_ID: {
          Uri mTrailerURI = MovieContract.TrailerEntry.buildTrailerUri(movieID);
          Log.v(LOG_TAG, mTrailerURI.toString());
          return getCursorLoader(mTrailerURI, TRAILER_COLUMNS);
        }

        case REVIEW_LOADER_ID: {
          Uri mReviewURI = MovieContract.ReviewEntry.buildReviewUri(movieID);
          Log.v(LOG_TAG, mReviewURI.toString());
          return getCursorLoader(mReviewURI, REVIEW_COLUMNS);
        }
      }
    }
    return null;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    switch (loader.getId()) {

      case MOVIE_LOADER_ID: {
        mAdapterMovieDetail.swapCursor(data);
        break;
      }

      case TRAILER_LOADER_ID: {

        if (Utils.isCursorEmpty(data) && !mTrailersLoaded) {

          String movieID = Long.toString(ContentUris.parseId(mMovieURI));

          FetchDetailTask detailTask = new FetchDetailTask(
              getActivity(),
              new APICallback() {
                @Override
                public void onCallCompleted() {
                  mTrailersLoaded = true;
                  restartLoader(TRAILER_LOADER_ID);
                }
              });
          detailTask.execute(movieID, getString(R.string.videos));
        }
        mTrailerAdapter.swapCursor(data);
        break;
      }

      case REVIEW_LOADER_ID: {

        if (Utils.isCursorEmpty(data) && !mReviewsLoaded) {

          String movieID = Long.toString(ContentUris.parseId(mMovieURI));

          FetchDetailTask detailTask = new FetchDetailTask(
              getActivity(),
              new APICallback() {
                @Override
                public void onCallCompleted() {
                  mReviewsLoaded = true;
                  restartLoader(REVIEW_LOADER_ID);
                }
              });
          detailTask.execute(movieID, getString(R.string.reviews));
        }
        mAdapterReviews.swapCursor(data);
        break;

      }
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {

    switch (loader.getId()) {

      case (MOVIE_LOADER_ID):
        mAdapterMovieDetail.swapCursor(null);
        break;

      case TRAILER_LOADER_ID:
        mTrailerAdapter.swapCursor(null);
        break;

      case REVIEW_LOADER_ID:
        mAdapterReviews.swapCursor(null);
        break;
    }
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    getLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);
    getLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();
    Log.d(LOG_TAG, "onStart");
  }

  public void onResume() {
    super.onResume();
    Log.d(LOG_TAG, "onResume");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    Bundle arguments = getArguments();
    if (arguments != null) {
      // Tablet UX
      mMovieURI = arguments.getParcelable(MOVIE_URI);
    } else {
      // Phone UX
      mMovieURI = getActivity().getIntent().getData();
    }

    mAdapterMovieDetail = new AdapterMovieDetail(getActivity(), null, 0);
    mTrailerAdapter = new AdapterTrailers(getActivity(), null, 0);
    mAdapterReviews = new AdapterReviews(getActivity(), null, 0);

    View rootView =
        inflater.inflate(R.layout.fragment_movie_detail_new, container, false);

    MergeAdapter mMergeAdapter = new MergeAdapter();
    mMergeAdapter.addAdapter(mAdapterMovieDetail);
    mMergeAdapter.addAdapter(mTrailerAdapter);
    mMergeAdapter.addAdapter(mAdapterReviews);

    ListView detailsListView = (ListView) rootView.findViewById(R.id.details_listview);
    detailsListView.setAdapter(mMergeAdapter);

    return rootView;
  }

  private void restartLoader(int loaderID) {
    if (isAdded()) {
      getLoaderManager().restartLoader(loaderID, null, this);
    }
  }
}
