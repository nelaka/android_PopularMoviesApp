package com.example.android.android_popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailersResponse implements Parcelable {
    public static final Parcelable.Creator<TrailersResponse> CREATOR
            = new Parcelable.Creator<TrailersResponse>() {
        public TrailersResponse createFromParcel(Parcel in) {
            return new TrailersResponse(in);
        }

        public TrailersResponse[] newArray(int size) {
            return new TrailersResponse[size];
        }
    };

    @SerializedName("id")
    private final String mId;
    @SerializedName("page")
    private final String mPage;
    @SerializedName("results")
    private final List<Trailer> mTrailers;
    @SerializedName("total_results")
    private final String mTotalResults;

    private TrailersResponse(Parcel in) {
        mId = in.readString();
        mPage = in.readString();
        mTrailers = in.readArrayList(Trailer.class.getClassLoader());
        mTotalResults = in.readString();
    }

    public List<Trailer> getTrailers() {
        return mTrailers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel out, int flags) {
        out.writeString(mId);
        out.writeString(mPage);
        out.writeList(mTrailers);
        out.writeString(mTotalResults);
    }
}