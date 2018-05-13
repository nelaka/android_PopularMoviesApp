package com.example.android.android_popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Trailer implements Parcelable {
    public static final Parcelable.Creator<Trailer> CREATOR
            = new Parcelable.Creator<Trailer>() {
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @SerializedName("name")
    private final String mName;
    @SerializedName("id")
    private final String mMediaId;
    @SerializedName("key")
    private final String mKey;
    @SerializedName("type")
    private final String mType;
    @SerializedName("site")
    private final String mSite;

    private Trailer(Parcel in) {
        mName = in.readString();
        mMediaId = in.readString();
        mKey = in.readString();
        mType = in.readString();
        mSite = in.readString();
    }

    public String getKey() {
        return mKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mName);
        out.writeString(mType);
        out.writeString(mMediaId);
        out.writeString(mKey);
        out.writeString(mSite);
    }
}