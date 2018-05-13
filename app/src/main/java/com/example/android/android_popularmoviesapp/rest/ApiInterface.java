package com.example.android.android_popularmoviesapp.rest;

import com.example.android.android_popularmoviesapp.model.MoviesResponse;
import com.example.android.android_popularmoviesapp.model.ReviewsResponse;
import com.example.android.android_popularmoviesapp.model.TrailersResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("movie/{sort_by}")
    Call<MoviesResponse> getMovies(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewsResponse> getReviews(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<TrailersResponse> getTrailers(@Path("id") int id, @Query("api_key") String apiKey);


}
