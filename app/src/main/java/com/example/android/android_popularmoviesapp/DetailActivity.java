package com.example.android.android_popularmoviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.android_popularmoviesapp.adapter.ReviewsAdapter;
import com.example.android.android_popularmoviesapp.adapter.TrailersAdapter;
import com.example.android.android_popularmoviesapp.data.MoviesContract;
import com.example.android.android_popularmoviesapp.model.Movie;
import com.example.android.android_popularmoviesapp.model.Review;
import com.example.android.android_popularmoviesapp.model.ReviewsResponse;
import com.example.android.android_popularmoviesapp.model.Trailer;
import com.example.android.android_popularmoviesapp.model.TrailersResponse;
import com.example.android.android_popularmoviesapp.rest.APIClient;
import com.example.android.android_popularmoviesapp.rest.ApiInterface;
import com.example.android.android_popularmoviesapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.android_popularmoviesapp.utils.Utils.YOU_TUBE_BASE_URL;

public class DetailActivity extends AppCompatActivity implements ReviewsAdapter.ReviewsAdapterOnClickHandler, TrailersAdapter.TrailersAdapterOnClickHandler {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String POSTER_IMAGES_URL = "http://image.tmdb.org/t/p";
    private static final String POSTER_WIDTH = "w185";

    @BindView(R.id.movie_poster_iv)
    ImageView posterView;
    @BindView(R.id.movie_title_tv)
    TextView movieTitle;
    @BindView(R.id.movie_desc_tv)
    TextView movieOverview;
    @BindView(R.id.movie_release_tv)
    TextView movieReleaseDate;
    @BindView(R.id.movie_vote_average_tv)
    TextView movieVoteAverage;
    @BindView(R.id.movie_vote_count_tv)
    TextView movieVoteCount;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.reviews_rv)
    RecyclerView mReviewsRV;
    @BindView(R.id.reviews_tv)
    TextView reviewsTextView;
    @BindView(R.id.trailers_rv)
    RecyclerView mTrailersRV;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;

    private ReviewsAdapter mReviewsAdapter;
    private TrailersAdapter mTrailersAdapter;
    private boolean mFavorite = false;
    private Movie mMovie;
    private List<Review> mReviews;
    private List<Trailer> mTrailers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent movieIntent = this.getIntent();

        if (movieIntent != null) {
            if (movieIntent.hasExtra("movie")) {
                mMovie = movieIntent.getExtras().getParcelable("movie");

                ButterKnife.bind(this);
                // Display the current selected movie title on the Action Bar
                getSupportActionBar().setTitle(mMovie.getTitle());

                String movieUrl = POSTER_IMAGES_URL + "/" + POSTER_WIDTH + mMovie.getBackdropPath();
                Picasso.with(posterView.getContext()).load(movieUrl)
                        .placeholder(R.drawable.movie_placeholder)
                        .error(R.drawable.movie_placeholder)
                        .into(posterView);

                movieTitle.setText(mMovie.getTitle());
                movieOverview.setText(mMovie.getOverview());
                movieReleaseDate.setText(mMovie.getReleaseDate());
                movieVoteAverage.setText(String.valueOf(mMovie.getVoteAverage()));
                movieVoteCount.setText(String.valueOf(mMovie.getVoteCount()));
            }
            mFavorite = isFavorite(mMovie);

            // Setup FAB to open EditorActivity
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mFavorite) {
                        fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                        removeMovieFromFavorites(mMovie);
                    } else {
                        fab.setImageResource(R.drawable.ic_favorite_white_24dp);
                        addMovieToFavorites(mMovie);
                    }
                }
            });

            List<Review> fakeList1 = new ArrayList<>();
            mReviewsAdapter = new ReviewsAdapter(getBaseContext(), this, fakeList1);
            apiRequest(Utils.theMovieDbApiKey, mReviewsRV, LinearLayoutManager.VERTICAL);

            List<Trailer> fakeList2 = new ArrayList<>();
            mTrailersAdapter = new TrailersAdapter(getBaseContext(), this, fakeList2);
            apiRequest(Utils.theMovieDbApiKey, mTrailersRV, LinearLayoutManager.HORIZONTAL);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /* Settings menu item clicked */
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareTrailerIntent();
            startActivity(shareIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private Intent createShareTrailerIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .getIntent();
        shareIntent.putExtra(Intent.EXTRA_TEXT, YOU_TUBE_BASE_URL + mTrailers.get(0).getKey());
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    public void apiRequest(String api_key, final RecyclerView mRecyclerView, int orientation) {
        if (api_key.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please obtain your API KEY first from themoviedb.org", Toast.LENGTH_LONG).show();
            return;
        }

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(mRecyclerView.getContext(), orientation, false);

        /* Association of the LayoutManager with the RecyclerView */
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Setting to improve performance when changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        ApiInterface apiService =
                APIClient.getClient().create(ApiInterface.class);

        if (mRecyclerView == mTrailersRV) {
            Call<TrailersResponse> call = apiService.getTrailers(mMovie.getPosterId(), api_key);

            call.enqueue(new Callback<TrailersResponse>() {

                @Override
                public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                    mTrailers = response.body().getTrailers();
                    Log.d(TAG, "Number of trailers received: " + mTrailers.size());

                    /* Setting the adapter attaches it to the RecyclerView in our layout. */
                    mTrailersAdapter.setData(mTrailers);
                    mTrailersRV.setAdapter(mTrailersAdapter);
                }

                @Override
                public void onFailure(Call<TrailersResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                }
            });
        } else {
            Call<ReviewsResponse> call = apiService.getReviews(mMovie.getPosterId(), api_key);
            call.enqueue(new Callback<ReviewsResponse>() {

                @Override
                public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                    mReviews = response.body().getReviews();
                    Log.d(TAG, "Number of reviews received: " + mReviews.size());

                    if (mReviews.size() > 0) reviewsTextView.setVisibility(View.VISIBLE);

                    /* Setting the adapter attaches it to the RecyclerView in our layout. */
                    mReviewsAdapter.setData(mReviews);
                    mReviewsRV.setAdapter(mReviewsAdapter);
                }

                @Override
                public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                }
            });
        }
    }

    private boolean isFavorite(Movie movie) {
        boolean isFavorite;
        String[] favoriteId = new String[]{String.valueOf(movie.getPosterId())};
        Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI, null, "id=?", favoriteId, null);

        if (cursor.getCount() > 0) {
            isFavorite = true;
            fab.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            isFavorite = false;
            fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }

        cursor.close();
        return isFavorite;
    }

    private void removeMovieFromFavorites(Movie movie) {
        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(movie.getPosterId())).build();

        int rowsDeleted = getContentResolver().delete(uri, null, null);

        // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
        Log.d(TAG, "rowsDeleted =" + rowsDeleted + " " + uri + " " + String.valueOf(movie.getTitle()));
        if (rowsDeleted > 0) {
            Toast.makeText(getBaseContext(), R.string.msg_removed_from_fav, Toast.LENGTH_LONG).show();
            getContentResolver().notifyChange(uri, null);
            mFavorite = false;
            setResult(RESULT_OK);
        }
    }

    private void addMovieToFavorites(Movie movie) {
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movie.getPosterId());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_DESC, movie.getOverview());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER, movie.getPosterUrl());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_USER_RATING, movie.getVoteAverage());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_DATE, movie.getReleaseDate());
        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            Toast.makeText(getBaseContext(), R.string.msg_added_to_fav, Toast.LENGTH_LONG).show();
            mFavorite = true;
        }
    }

    @Override
    public void onClick(Review review) {
        String url = review.getReviewUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onClick(Trailer trailer) {
        String url = YOU_TUBE_BASE_URL + trailer.getKey();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}