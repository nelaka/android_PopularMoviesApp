package com.example.android.android_popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {
    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    @SerializedName("poster_path")
    private String mPosterUrl;
    @SerializedName("overview")
    private String mOverview; //plot synopsis
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("id")
    private Integer mPosterId;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("backdrop_path")
    private String mBackdropPath;
    @SerializedName("vote_count")
    private Integer mVoteCount;    //vote count
    @SerializedName("vote_average")     //user rating
    private Float mVoteAverage;

    public Movie(String title, String posterUrl, int posterId, String backdropPath, String releaseDate, String overview, float voteAverage, int voteCount) {
        mTitle = title;
        mPosterUrl = posterUrl;
        mPosterId = posterId;
        mBackdropPath = backdropPath;
        mReleaseDate = releaseDate;
        mOverview = overview;
        mVoteAverage = voteAverage;
        mVoteCount = voteCount;
    }

    private Movie(Parcel in) {
        mTitle = in.readString();
        mPosterUrl = in.readString();
        mPosterId = in.readInt();
        mBackdropPath = in.readString();
        mReleaseDate = in.readString();
        mOverview = in.readString();
        mVoteAverage = in.readFloat();
        mVoteCount = in.readInt();
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public int getPosterId() { return mPosterId; }

    public String getOverview() {
        return mOverview;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public float getVoteAverage() {
        return mVoteAverage;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mTitle);
        out.writeString(mPosterUrl);
        out.writeInt(mPosterId);
        out.writeString(mBackdropPath);
        out.writeString(mReleaseDate);
        out.writeString(mOverview);
        out.writeFloat(mVoteAverage);
        out.writeInt(mVoteCount);
    }
}