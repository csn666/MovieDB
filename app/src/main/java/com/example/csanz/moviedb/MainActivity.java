package com.example.csanz.moviedb;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;


import com.example.csanz.moviedb.Adapter.RecyclerViewAdapter;
import com.example.csanz.moviedb.Data.Movie;
import com.example.csanz.moviedb.Syn.ServiceClientImpl;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


import static com.example.csanz.moviedb.Syn.ServiceClientImpl.movies;

public class MainActivity extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerViewMovies)
    RecyclerView listMovies;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.swipyrefreshlayout)
    SwipyRefreshLayout swipeContainer;

    private int numPage=1;
    private ServiceClientImpl serviceClient;
    private RecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        swipeContainer.setOnRefreshListener(this);

        //Connect to api TMDB
        serviceClient = new ServiceClientImpl();
        serviceClient.connectAPI();

        new showMovies().execute();
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        //swipeContainer.clearAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new showMovies().execute();
            }
        },2000);
    }

    private class showMovies extends AsyncTask<Void, Void, List<Movie>>{

        @Override
        protected void onPreExecute(){
            showProgressBar(true);
        }

        @Override
        protected List<Movie> doInBackground(Void... voids) {
            //Get Movies
            return serviceClient.getListMovies(numPage);
        }

        @Override
        protected void onPostExecute(List<Movie> list){
            if(list!=null && list.size()>0){
                setupAdapter(list);
                numPage += 1;
            }
            showProgressBar(false);
        }
    }

    private void showProgressBar(boolean show){
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupAdapter(List<Movie> list){
        adapter = new RecyclerViewAdapter(list, getApplicationContext());
        this.listMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.listMovies.setAdapter(adapter);
    }
}
