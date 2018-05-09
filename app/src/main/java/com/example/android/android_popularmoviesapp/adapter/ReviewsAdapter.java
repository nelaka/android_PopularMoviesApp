package com.example.android.android_popularmoviesapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.android.android_popularmoviesapp.R;
import com.example.android.android_popularmoviesapp.model.Review;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {
    private final Context mContext;
    private List<Review> mReviews;
    private static final String TAG = ReviewsAdapter.class.getSimpleName();
    private final ReviewsAdapterOnClickHandler mClickHandler;

    public ReviewsAdapter(Context context, ReviewsAdapterOnClickHandler clickHandler, List<Review> reviews) {
        mContext = context;
        mReviews = reviews;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public ReviewsAdapter.ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_list_item, viewGroup, false);
        view.setFocusable(true);
        return new ReviewsViewHolder(view, mClickHandler, mReviews);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.reviewAuthorView.setText("by " + review.getAuthor());
        holder.reviewTextView.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if (null == mReviews) return 0;
        return mReviews.size();
    }

    /**
     * This method is used to set the movies on a MoviesAdapter if we've already
     * created one.
     *
     * @param reviewsData The new movie data to be displayed.
     */
    public void setData(List<Review> reviewsData) {
        mReviews = reviewsData;
        notifyDataSetChanged();
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface ReviewsAdapterOnClickHandler {
        void onClick(Review review);
    }


    class ReviewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ReviewsAdapterOnClickHandler mClickHandler;
        @BindView(R.id.review_author_tv)
        TextView reviewAuthorView;
        @BindView(R.id.review_tv)
        TextView reviewTextView;

        public ReviewsViewHolder(View view, ReviewsAdapterOnClickHandler clickHandler, List<Review> reviews) {
            super(view);
            mClickHandler = clickHandler;
            mReviews = reviews;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mReviews.get(adapterPosition));
        }
    }
}




