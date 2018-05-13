package com.example.android.android_popularmoviesapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.android_popularmoviesapp.adapter.FavMoviesAdapter;
import com.example.android.android_popularmoviesapp.adapter.MoviesAdapter;
import com.example.android.android_popularmoviesapp.data.MoviesContract;
import com.example.android.android_popularmoviesapp.model.Movie;
import com.example.android.android_popularmoviesapp.model.MoviesResponse;
import com.example.android.android_popularmoviesapp.rest.APIClient;
import com.example.android.android_popularmoviesapp.rest.ApiInterface;
import com.example.android.android_popularmoviesapp.utils.MoviesPreferences;
import com.example.android.android_popularmoviesapp.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class MainActivityFragment extends Fragment implements MoviesAdapter.MoviesAdapterOnClickHandler,
        FavMoviesAdapter.FavMoviesAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int CHANGES_IN_FAV_MOVIES = 1;  // The request code
    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
    @BindView(R.id.movies_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;
    @BindView(R.id.tv_no_fav_movies_display)
    TextView mNoFavMovieMessageDisplay;
    private ArrayList<Movie> mMovies, mFavMovies;
    private MoviesAdapter mMoviesAdapter;
    private FavMoviesAdapter mFavMoviesAdapter;
    private Context mContext;
    RecyclerView.OnScrollListener mScrollListener;
    private int mPosition;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        mMoviesAdapter = new MoviesAdapter(mContext, this);
        mFavMoviesAdapter = new FavMoviesAdapter(mContext, this);

        moviesRequest();

        PreferenceManager.getDefaultSharedPreferences(mContext)
                .registerOnSharedPreferenceChangeListener(this);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra("movie", movie);

        mContext.startActivity(intent);
        startActivityForResult(intent, CHANGES_IN_FAV_MOVIES);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        mNoFavMovieMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showNoFavMovieMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, show the no favorite movie message */
        mNoFavMovieMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mNoFavMovieMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the movies are visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(TAG, "onStart: preferences were updated");
            moviesRequest();
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void moviesRequest() {
        String sortBy;
        String API_KEY = Utils.theMovieDbApiKey;

        if (API_KEY.isEmpty()) {
            Toast.makeText(mContext, "Please obtain your API KEY first from themoviedb.org", Toast.LENGTH_LONG).show();
            return;
        }

        final int noOfColumns = getResources().getInteger(R.integer.no_of_columns);
        GridLayoutManager layoutManager =
                new GridLayoutManager(mContext, noOfColumns);

        /* Association of the LayoutManager with the RecyclerView */
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Setting to improve performance when changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(mScrollListener);
        sortBy = MoviesPreferences.getPreferredSortBy(mContext);

        if (!sortBy.equals(mContext.getString(R.string.pref_sort_by_fav))) {

            ApiInterface apiService =
                    APIClient.getClient().create(ApiInterface.class);
            Call<MoviesResponse> call = apiService.getMovies(sortBy, API_KEY);
            call.enqueue(new Callback<MoviesResponse>() {

                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {

                    mMovies = response.body().getResults();
                    Log.d(TAG, "Number of results received: " + mMovies.size());

                    /* Setting the adapter attaches it to the RecyclerView in our layout. */
                    mMoviesAdapter.setMoviesData(mMovies);
                    mRecyclerView.setAdapter(mMoviesAdapter);

                    /* Setting the adapter attaches it to the RecyclerView in our layout. */
                    mRecyclerView.setAdapter(mMoviesAdapter);
                    showMovieDataView();
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    showErrorMessage();
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                }
            });
        } else {
            moviesFromDB();
        }
    }

    private void moviesFromDB() {
        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor moviesResponse = resolver.query(uri, null, null, null, null);

        if (moviesResponse.getCount() <= 0) {
            showNoFavMovieMessage();
            return;
        }
        // Create an empty ArrayList that we can start adding movies to
        ArrayList<Movie> movies = new ArrayList<>();

        for (int i = 0; i < moviesResponse.getCount(); i++) {
            int movieTitleIndex = moviesResponse.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE);
            int movieIdIndex = moviesResponse.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID);
            int posterPathIndex = moviesResponse.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER);
            int backdropPathIndex = moviesResponse.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH);
            int releaseDateIndex = moviesResponse.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_DATE);
            int overviewIndex = moviesResponse.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_DESC);
            int voteAverageIndex = moviesResponse.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_USER_RATING);
            int voteCountIndex = moviesResponse.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT);

            moviesResponse.moveToPosition(i);

            movies.add(new Movie(
                    moviesResponse.getString(movieTitleIndex),
                    moviesResponse.getString(posterPathIndex),
                    moviesResponse.getInt(movieIdIndex),
                    moviesResponse.getString(backdropPathIndex),
                    moviesResponse.getString(releaseDateIndex),
                    moviesResponse.getString(overviewIndex),
                    moviesResponse.getFloat(voteAverageIndex),
                    moviesResponse.getInt(voteCountIndex)
            ));
        }
        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mFavMoviesAdapter.setMoviesData(movies);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mFavMoviesAdapter);
        showMovieDataView();
        moviesResponse.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == CHANGES_IN_FAV_MOVIES) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                moviesRequest();
            }
        }
    }
}