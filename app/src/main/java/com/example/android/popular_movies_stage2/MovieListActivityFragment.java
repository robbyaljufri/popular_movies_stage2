package com.example.android.popular_movies_stage2;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.android.popular_movies_stage2.adapters.MovieDetailAdapter;
import com.example.android.popular_movies_stage2.adapters.MovieListAdapter;
import com.example.android.popular_movies_stage2.callbacks.APICallback;
import com.example.android.popular_movies_stage2.callbacks.ItemSelectedCallback;
import com.example.android.popular_movies_stage2.data.MovieContract;
import com.example.android.popular_movies_stage2.download.FetchMoviesTask;


public class MovieListActivityFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

  private final String LOG_TAG = MovieListActivityFragment.class.getSimpleName();

  private static final int MOVIES_LOADER = 0;

  private static final String[] MOVIE_COLUMNS = {
      MovieContract.MovieEntry.COLUMN_ID,
      MovieContract.MovieEntry.COLUMN_MOVIE_ID,
      MovieContract.MovieEntry.COLUMN_TITLE,
      MovieContract.MovieEntry.COLUMN_OVERVIEW,
      MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
      MovieContract.MovieEntry.COLUMN_POPULARITY,
      MovieContract.MovieEntry.COLUMN_RATING,
      MovieContract.MovieEntry.COLUMN_SOURCE,
      MovieContract.MovieEntry.COLUMN_POSTER_PATH,
      MovieContract.MovieEntry.COLUMN_IS_FAVORITE
  };

  public static final int COL_ID = 0;
  public static final int COL_MOVIE_ID = 1;
  public static final int COL_TITLE = 2;
  public static final int COL_OVERVIEW = 3;
  public static final int COL_RELEASE_DATE = 4;
  public static final int COL_POPULARITY = 5;
  public static final int COL_RATING = 6;
  public static final int COL_SOURCE = 7;
  public static final int COL_POSTER_PATH = 8;
  public static final int COL_IS_FAVORITE = 9;

  private MovieListAdapter mMovieListAdapter;
  private int mPosition;

  public MovieListActivityFragment() {
    //empty
  }

  private CursorLoader getCursorLoader(String selection, String[] args,
                                       String sortOrder) {
    return new CursorLoader(
        getActivity(),
        MovieContract.MovieEntry.CONTENT_URI,
        MOVIE_COLUMNS,
        selection,
        args,
        sortOrder
    );
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {

    String sortOrder = Utils.getSortOrder(getActivity());

    if (sortOrder.equals(getString(R.string.movie_sort_favorites))) {

      return getCursorLoader(
          MovieContract.MovieEntry.COLUMN_IS_FAVORITE + " = ?",
          new String[]{"1"},
          MovieContract.MovieEntry.COLUMN_TITLE + " ASC"
      );

    } else if (sortOrder.equals(getString(R.string.movie_sort_popularity))) {

      return getCursorLoader(
          MovieContract.MovieEntry.COLUMN_SOURCE + " = ?",
          new String[]{sortOrder},
          MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC"
      );

    } else if (sortOrder.equals(getString(R.string.movie_sort_rating))) {

      return getCursorLoader(
          MovieContract.MovieEntry.COLUMN_SOURCE + " = ? ",
          new String[]{sortOrder},
          MovieContract.MovieEntry.COLUMN_RATING + " DESC"
      );

    }
    return null;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    Log.d(LOG_TAG, DatabaseUtils.dumpCursorToString(data));

    String sortOrder = Utils.getSortOrder(getActivity());
    String faves = getString(R.string.movie_sort_favorites);

    if (Utils.isCursorEmpty(data) && !sortOrder.equals(faves)) {
      getMovies();
    }

    mMovieListAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    mMovieListAdapter.swapCursor(null);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_movie_list, menu);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    getLoaderManager().initLoader(MOVIES_LOADER, null, this);
    super.onActivityCreated(savedInstanceState);
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View movieListView =
        inflater.inflate(R.layout.fragment_movie_list, container, false);

    GridView gridview =
        (GridView) movieListView.findViewById(R.id.movie_poster_grid);

    mMovieListAdapter = new MovieListAdapter(getActivity(), null, 0);

    TextView view = (TextView) movieListView.findViewById(R.id.no_movies);

    gridview.setAdapter(mMovieListAdapter);

    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView adapterView, View view, int position,
                              long l) {

        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        if (cursor != null) {

          ((ItemSelectedCallback) getActivity()).onItemSelected(
              MovieContract.MovieEntry
                  .buildMovieUri(cursor.getLong(COL_MOVIE_ID))
          );
          mPosition = position;
        }
      }
    });

    gridview.setEmptyView(view);

    return movieListView;
  }

  @Override
  public void onCreate(Bundle savedInstanceState){
    Log.d(LOG_TAG, "onCreate");
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onResume() {
    Log.d(LOG_TAG, "onResume");
    super.onResume();
    Context context = getContext();
    String sortOrder = Utils.getSortOrder(context);
    if (sortOrder.equals(getString(R.string.movie_sort_favorites))) {

      SharedPreferences prefs
          = PreferenceManager.getDefaultSharedPreferences(context);
      Boolean updated = prefs.contains(MovieDetailAdapter.FAVORITES_UPDATED);
      if (updated) {
        restartLoader();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(MovieDetailAdapter.FAVORITES_UPDATED);
        editor.commit();
      }
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    Log.d(LOG_TAG, "onSaveInstanceState");
    super.onSaveInstanceState(outState);
  }

  public void onSortOrderChanged() {

    String sortOrder = Utils.getSortOrder(getActivity());
    if (!sortOrder.equals(getString(R.string.movie_sort_favorites))) {
      getMovies();
    } else {
      restartLoader();
    }
  }

  private void getMovies() {

    String sortOrder = Utils.getSortOrder(getActivity());
    FetchMoviesTask movieTask = new FetchMoviesTask(
        getActivity(),
        new APICallback() {
          @Override
          public void onCallCompleted() {
            restartLoader();
          }
        }
    );
    movieTask.execute(sortOrder);
  }

  private void restartLoader() {
    if (isAdded()) {
      getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }
  }

}