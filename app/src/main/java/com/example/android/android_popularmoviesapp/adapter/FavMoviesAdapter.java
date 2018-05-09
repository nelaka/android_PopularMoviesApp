package com.example.android.android_popularmoviesapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.android_popularmoviesapp.R;
import com.example.android.android_popularmoviesapp.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavMoviesAdapter extends RecyclerView.Adapter<FavMoviesAdapter.FavMoviesViewHolder> {
    private static final String TAG = FavMoviesAdapter.class.getSimpleName();
    private static final String POSTER_WIDTH = "w185";
    private static final String POSTER_IMAGES_URL = "http://image.tmdb.org/t/p";

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context mContext;
    private final Cursor mCursor;
    private final FavMoviesAdapterOnClickHandler mClickHandler;
    private List<Movie> mFavMovies;

    public FavMoviesAdapter(@NonNull Context context, Cursor cursor, FavMoviesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mCursor = cursor;
        mClickHandler = clickHandler;
    }

    public FavMoviesAdapter(@NonNull Context context, List<Movie> movies, Cursor cursor, FavMoviesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mFavMovies = movies;
        mCursor = cursor;
        mClickHandler = clickHandler;

    }



    @NonNull
    @Override
    public FavMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, viewGroup, false);

        view.setFocusable(true);
        return new FavMoviesViewHolder(view, mClickHandler, mFavMovies);
    }

    @Override
    public void onBindViewHolder(@NonNull FavMoviesViewHolder holder, int position) {
        Movie movie = mFavMovies.get(position);

        Log.d(TAG, "fav poster: " + mFavMovies.get(0).getTitle());
        String movieUrl = POSTER_IMAGES_URL + "/" + POSTER_WIDTH + movie.getPosterUrl();
        Picasso.with(holder.posterView.getContext()).load(movieUrl)
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.no_image)
                .into(holder.posterView);
    }

    @Override
    public int getItemCount() {
        if (null == mFavMovies) return 0;
        return mFavMovies.size();
    }

    /**
     * This method is used to set the movies on a MoviesAdapter if we've already
     * created one.
     *
     * @param moviesData The new movie data to be displayed.
     */
    public void setMoviesData(List<Movie> moviesData) {
        mFavMovies = moviesData;
        notifyDataSetChanged();
    }


    /**
     * The interface that receives onClick messages.
     */
    public interface FavMoviesAdapterOnClickHandler {
        void onClick(Movie movie);
    }


    class FavMoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final FavMoviesAdapterOnClickHandler mClickHandler;
        @BindView(R.id.list_item_poster)
        ImageView posterView;
        private List<Movie> mFavMovies;

        public FavMoviesViewHolder(View view, FavMoviesAdapterOnClickHandler clickHandler, List<Movie> movies) {
            super(view);
            mClickHandler = clickHandler;
            mFavMovies = movies;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mFavMovies.get(adapterPosition));
        }

    }

}
