package com.example.android.android_popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewsResponse implements Parcelable {
    public static final Parcelable.Creator<ReviewsResponse> CREATOR
            = new Parcelable.Creator<ReviewsResponse>() {
        public ReviewsResponse createFromParcel(Parcel in) {
            return new ReviewsResponse(in);
        }

        public ReviewsResponse[] newArray(int size) {
            return new ReviewsResponse[size];
        }
    };

    @SerializedName("id")
    private String mId;
    @SerializedName("page")
    private String mPage;
    @SerializedName("results")
    private List<Review> mReviews;
    @SerializedName("total_results")
    private String mTotalResults;

    public ReviewsResponse(String id, String page, List<Review> reviews, String totalResults) {
        mId = id;
        mPage = page;
        mReviews = reviews;
        mTotalResults = totalResults;
    }

    private ReviewsResponse(Parcel in) {
        mId = in.readString();
        mPage = in.readString();
        mReviews = in.readArrayList(Review.class.getClassLoader());
        mTotalResults = in.readString();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public List<Review> getReviews() {
        return mReviews;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mId);
        out.writeString(mPage);
        out.writeList(mReviews);
        out.writeString(mTotalResults);
    }
}