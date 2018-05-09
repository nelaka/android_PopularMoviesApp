package com.example.android.android_popularmoviesapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.android.android_popularmoviesapp.R;

public class MoviesPreferences {
    private static final String TAG = MoviesPreferences.class.getSimpleName();

    public static String getPreferredSortBy(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);


        String keyForSortBy = context.getString(R.string.pref_sort_by_key);
        String defaultSortBy = context.getString(R.string.pref_sort_by_default);

        return sp.getString(keyForSortBy, defaultSortBy);
    }
}
