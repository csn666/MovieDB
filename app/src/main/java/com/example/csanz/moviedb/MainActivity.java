package com.example.csanz.moviedb;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.csanz.moviedb.Adapter.RecyclerViewAdapter;
import com.example.csanz.moviedb.Data.Movie;
import com.example.csanz.moviedb.RefreshRecycler.EndlessRecycler;
import com.example.csanz.moviedb.Syn.ServiceClientImpl;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener, RecyclerViewAdapter.newSearch {

    @BindView(R.id.recyclerViewMovies)
    RecyclerView listMovies;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.editSearch)
    EditText editSearch;
    @BindView(R.id.imgClearSearch)
    ImageButton btnClearSearch;
    @BindView(R.id.swipeContainer)
    SwipyRefreshLayout swipeContainer;

    private int numPage=1;
    private ServiceClientImpl serviceClient;
    private RecyclerViewAdapter adapter;
    CharSequence charSequence=null;
    public boolean isSwipe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        swipeContainer.setOnRefreshListener(this);

        //Connect to api TMDB
        serviceClient = new ServiceClientImpl();
        serviceClient.connectAPI();

        //Setup adapter
        setupAdapter();
        //Execute async task to get movies from TMDB
        new showMovies().execute();

        //Add watcher to EditText
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
                charSequence = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty())
                {
                    if(btnClearSearch.getVisibility()==View.INVISIBLE)
                        btnClearSearch.setVisibility(View.VISIBLE);

                }else{
                    if(btnClearSearch.getVisibility()==View.VISIBLE)
                        btnClearSearch.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        isSwipe = true;
        new  Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new showMovies().execute();
            }
        },10);

    }

    /*
     * Method async to get movies from database TMDB
     */
    private class showMovies extends AsyncTask<Void, Void, List<Movie>>{

        @Override
        protected void onPreExecute(){
            if(!isSwipe)
                showProgressBar(true);
        }

        @Override
        protected List<Movie> doInBackground(Void... voids) {
            //Get Movies
            return serviceClient.getListMovies(numPage);
        }

        @Override
        protected void onPostExecute(List<Movie> list){
            swipeContainer.setRefreshing(false);
            if(list!=null && list.size()>0){
                adapter.setMovies(list);

                if(charSequence!=null && charSequence!=""){
                    adapter.getFilter().filter(charSequence);
                }else{
                    adapter.getFilter().filter("");
                }

                numPage += 1;
            }

            if(!isSwipe)
                showProgressBar(false);

            isSwipe = false;
        }
    }

    /*
     * Clear results search
     */
    @OnClick(R.id.imgClearSearch)
    public void clearSearch(){
        editSearch.setText("");
        adapter.getFilter().filter(editSearch.getText().toString());
        btnClearSearch.setVisibility(View.INVISIBLE);
    }

    /*
     * Show or hide progressbar
     */
    private void showProgressBar(boolean show){
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    /*
     * Setup adapter movies
     */
    private void setupAdapter(){
        adapter = new RecyclerViewAdapter(new ArrayList<Movie>(), MainActivity.this);
        this.listMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.listMovies.setAdapter(adapter);
    }

    /*
     * This method return if is the end of recycleview
     */
    private boolean isEndRecyclerView(){
        LinearLayoutManager layoutManager = ((LinearLayoutManager)listMovies.getLayoutManager());
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        return ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0);
    }

    public void newSearch(){
        isSwipe=true;
        new showMovies().execute();
    }
}
