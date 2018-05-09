package com.example.android.android_popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MoviesResponse implements Parcelable {

    public static final Parcelable.Creator<MoviesResponse> CREATOR
            = new Parcelable.Creator<MoviesResponse>() {
        public MoviesResponse createFromParcel(Parcel in) {
            return new MoviesResponse(in);
        }

        public MoviesResponse[] newArray(int size) {
            return new MoviesResponse[size];
        }
    };
    @SerializedName("page")
    private int mPage;
    @SerializedName("results")
    private ArrayList<Movie> mResults;
    @SerializedName("total_results")
    private int mTotalResults;
    @SerializedName("total_pages")
    private int mTotalPages;

    private MoviesResponse(Parcel in) {
        mPage = in.readInt();
        mResults = in.readArrayList(Movie.class.getClassLoader());
        mTotalResults = in.readInt();
        mTotalPages = in.readInt();
    }

    public ArrayList<Movie> getResults() {
        return mResults;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mPage);
        out.writeList(mResults);
        out.writeInt(mTotalPages);
        out.writeInt(mTotalPages);
    }
}