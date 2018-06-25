package com.example.csanz.moviedb.Syn;

import com.example.csanz.moviedb.Data.Movie;
import com.example.csanz.moviedb.Data.MovieResults;
import com.example.csanz.moviedb.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.csanz.moviedb.Data.Constants.CATEGORY;
import static com.example.csanz.moviedb.Data.Constants.KEY;
import static com.example.csanz.moviedb.Data.Constants.URL_MOVIES;

public class ServiceClientImpl {

    private Retrofit retrofit;
    private IServiceClient iService;

    /*
     * Connect to TMDB
     */
    public void connectAPI(){
        retrofit = new Retrofit.Builder()
                .baseUrl(URL_MOVIES)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /*
     * Return list of most popular movies
     */
    public List<Movie> getListMovies(int page){
        iService = retrofit.create(IServiceClient.class);
        List<Movie> list = new ArrayList<>();
        Call<MovieResults> call = iService.getListMovies(CATEGORY,KEY,page);
        try {
            MovieResults movieResults = call.execute().body();
            list = movieResults.getResults();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
