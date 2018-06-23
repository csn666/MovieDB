package com.example.csanz.moviedb.Syn;

import com.example.csanz.moviedb.Data.Movie;
import com.example.csanz.moviedb.Data.MovieResults;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IServiceClient {
    //Get list movies
    @GET("3/movie/{category}")
    Call<MovieResults> getListMovies(@Path("category") String category,
                                     @Query("api_key") String apiKey,
                                     @Query("page") int page);
}
