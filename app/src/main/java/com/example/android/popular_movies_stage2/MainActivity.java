package com.example.android.popular_movies_stage2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popular_movies_stage2.panggil.ItemSelectedCallback;


public class MainActivity extends AppCompatActivity
        implements ItemSelectedCallback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "MDF_TAG";

    private String mSortOrder;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSortOrder = Utils.getSortOrder(this);

        if (findViewById(R.id.movie_detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(
                                R.id.movie_detail_container,
                                new MovieDetailFragment(),
                                MOVIE_DETAIL_FRAGMENT_TAG)
                        .commit();
            }

        } else {

            mTwoPane = false;
            getSupportActionBar().setElevation(0f);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_list, menu);
        return true;
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "onResume");

        super.onResume();
        String sortOrder = Utils.getSortOrder(this);

        if (Utils.sortOrderChanged(mSortOrder, sortOrder)) {

            MovieListActivityFragment mlaFragment =
                    (MovieListActivityFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.list_fragment);

            if (mlaFragment != null)
                mlaFragment.onSortOrderChanged();

            mSortOrder = sortOrder;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected");

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri contentUri) {

        Log.d(LOG_TAG, "onItemSelected");

        if  (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.MOVIE_URI, contentUri);

            MovieDetailFragment df = new MovieDetailFragment();
            df.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, df, MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class).setData(contentUri);
            startActivity(intent);
        }
    }
}
