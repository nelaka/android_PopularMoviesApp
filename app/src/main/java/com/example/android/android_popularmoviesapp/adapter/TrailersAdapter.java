package com.example.android.android_popularmoviesapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.android_popularmoviesapp.R;
import com.example.android.android_popularmoviesapp.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context mContext;
    private List<Trailer> mTrailers;
    private final TrailersAdapterOnClickHandler mClickHandler;

    public TrailersAdapter(Context context, TrailersAdapterOnClickHandler clickHandler, List<Trailer> trailers) {
        mContext = context;
        mTrailers = trailers;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public TrailersAdapter.TrailersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trailer_list_item, viewGroup, false);

        view.setFocusable(true);
        return new TrailersViewHolder(view, mClickHandler, mTrailers);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersViewHolder holder, int position) {
        Trailer trailer = mTrailers.get(position);

        String img_url = "https://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg"; // this is link which will give u thumbnail image of that video

        Picasso.with(holder.trailerView.getContext()).load(img_url)
                .placeholder(R.drawable.ic_launcher)
                .into(holder.trailerView);
    }

    @Override
    public int getItemCount() {
        if (null == mTrailers) return 0;
        return mTrailers.size();
    }

    /**
     * This method is used to set the movies on a MoviesAdapter if we've already
     * created one.
     *
     * @param trailerData The new movie data to be displayed.
     */
    public void setData(List<Trailer> trailerData) {
        mTrailers = trailerData;
        notifyDataSetChanged();
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface TrailersAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }


    class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TrailersAdapterOnClickHandler mClickHandler;
        @BindView(R.id.trailer_iv)
        ImageView trailerView;

        public TrailersViewHolder(View view, TrailersAdapterOnClickHandler clickHandler, List<Trailer> trailers) {
            super(view);
            mClickHandler = clickHandler;
            mTrailers = trailers;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mTrailers.get(adapterPosition));
        }
    }
}