package com.example.csanz.moviedb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.csanz.moviedb.Data.Constants.URL_MOVIES;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerViewMovies)
    RecyclerView listMovies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


    }

    private void connectAPI(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_MOVIES)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
