package com.example.csanz.moviedb.Syn;

import com.example.csanz.moviedb.Data.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IServiceClient {
    //Get list movies
    @GET("list/{listId}")
    Call<List<Movie>> getListMovies(@Path("id") int id);
}
