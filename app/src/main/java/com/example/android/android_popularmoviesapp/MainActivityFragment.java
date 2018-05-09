package com.example.android.android_popularmoviesapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class MainActivityFragment extends Fragment implements MoviesAdapter.MoviesAdapterOnClickHandler,
        FavMoviesAdapter.FavMoviesAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = MainActivityFragment.class.getSimpleName();

    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
    @BindView(R.id.movies_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;
    private ArrayList<Movie> mMovies, mFavMovies;
    private MoviesAdapter mMoviesAdapter;
    private FavMoviesAdapter mFavMoviesAdapter;
    private Context mContext;
    private Cursor mCursor;
    private String mSortBy;
    private MoviesResponse mResults;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
        } else {
            mMovies = savedInstanceState.getParcelableArrayList("movies");
            mFavMovies = savedInstanceState.getParcelableArrayList("fav_movies");
        }
        mContext = getActivity();


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", mMovies);
        outState.putParcelableArrayList("fav_movies", mFavMovies);

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        //List<Movie> fakeList = new ArrayList<>();

        mSortBy = MoviesPreferences.getPreferredSortBy(mContext);
        String favSortBy = mContext.getString(R.string.pref_sort_by_fav);
        mMoviesAdapter = new MoviesAdapter(mContext, /*fakeList,*/ this);
        mFavMoviesAdapter = new FavMoviesAdapter(mContext, /* fakeList,*/ mCursor, this);
      //  if (!favSortBy.equals(mSortBy)) {
            moviesRequest(Utils.theMovieDbApiKey, mSortBy);
       // } else {

         //   moviesFromDB();
       // }

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
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);

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
            moviesRequest(Utils.theMovieDbApiKey, mSortBy);

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

    public void moviesRequest(String api_key, String sortBy) {
        if (api_key.isEmpty()) {
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

        sortBy = MoviesPreferences.getPreferredSortBy(mContext);

        if (sortBy.equals(mContext.getString(R.string.pref_sort_by_fav))) {
            moviesFromDB();

        }
        else{

            ApiInterface apiService =
                    APIClient.getClient().create(ApiInterface.class);

            Call<MoviesResponse> call = apiService.getMovies(sortBy, api_key);

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
        }


    }

    public void moviesFromDB() {

        //   mMovies = new ArrayList<>();

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
        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor moviesResponse = resolver.query(uri, null, null, null, null);
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

            Log.d(TAG, "fav movie: " + movies.get(i).getTitle());
        }
        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mFavMoviesAdapter.setMoviesData(movies);


        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mFavMoviesAdapter);
        showMovieDataView();


    }

}