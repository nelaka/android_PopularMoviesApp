package com.example.android.android_popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Review implements Parcelable {
    public static final Parcelable.Creator<Review> CREATOR
            = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @SerializedName("author")
    private String mAuthor;
    @SerializedName("content")
    private String mContent;
    @SerializedName("id")
    private String mId;
    @SerializedName("url")
    private String mUrl;

    public Review(String author, String content, String id, String url) {
        mAuthor = author;
        mContent = content;
        mId = id;
        mUrl = url;
    }

    private Review(Parcel in) {
        mAuthor = in.readString();
        mContent = in.readString();
        mId = in.readString();
        mUrl = in.readString();
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public String getId() {
        return mId;
    }

    public String getReviewUrl() {
        return mUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mAuthor);
        out.writeString(mContent);
        out.writeString(mId);
        out.writeString(mUrl);
    }
}

