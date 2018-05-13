package com.example.android.android_popularmoviesapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.android_popularmoviesapp.R;
import com.example.android.android_popularmoviesapp.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private static final String POSTER_WIDTH = "w185";
    private static final String POSTER_IMAGES_URL = "http://image.tmdb.org/t/p";
    @BindView(R.id.list_item_poster)
    ImageView posterView;
    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context mContext;
    private final MoviesAdapterOnClickHandler mClickHandler;
    private List<Movie> mMovies = new ArrayList<>();

    public MoviesAdapter(@NonNull Context context, MoviesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MoviesAdapter.MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, viewGroup, false);

        view.setFocusable(true);
        return new MoviesViewHolder(view, mClickHandler, mMovies);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder moviesViewHolder, int position) {
        Movie movie = mMovies.get(position);

        String movieUrl = POSTER_IMAGES_URL + "/" + POSTER_WIDTH + movie.getPosterUrl();
        Picasso.with(moviesViewHolder.posterView.getContext()).load(movieUrl)
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder)
                .into(moviesViewHolder.posterView);
    }

    @Override
    public int getItemCount() {
        if (null == mMovies) return 0;
        return mMovies.size();
    }

    /**
     * This method is used to set the movies on a MoviesAdapter if we've already
     * created one.
     *
     * @param moviesData The new movie data to be displayed.
     */
    public void setMoviesData(List<Movie> moviesData) {
        mMovies = moviesData;
        notifyDataSetChanged();
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movie);
    }


    class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MoviesAdapter.MoviesAdapterOnClickHandler mClickHandler;
        @BindView(R.id.list_item_poster)
        ImageView posterView;
        private final List<Movie> mMovies;

        public MoviesViewHolder(View view, MoviesAdapter.MoviesAdapterOnClickHandler clickHandler, List<Movie> movies) {
            super(view);
            mClickHandler = clickHandler;
            mMovies = movies;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mMovies.get(adapterPosition));
        }
    }
}