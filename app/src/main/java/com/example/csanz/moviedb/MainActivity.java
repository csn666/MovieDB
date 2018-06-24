package com.example.csanz.moviedb;

import android.os.AsyncTask;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerViewMovies)
    RecyclerView listMovies;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.editSearch)
    EditText editSearch;
    @BindView(R.id.imgClearSearch)
    ImageButton btnClearSearch;

    private int numPage=1;
    private ServiceClientImpl serviceClient;
    private RecyclerViewAdapter adapter;
    CharSequence charSequence=null;
    public boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

        //Scroll listener of recyclerview
        listMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(isEndRecyclerView()){
                    if(!isLoading) {
                        Log.d("Page:", String.valueOf(numPage));
                        new showMovies().execute();

                    }
                }
            }
        });
    }

    /*
     * Method async to get movies from database TMDB
     */
    private class showMovies extends AsyncTask<Void, Void, List<Movie>>{

        @Override
        protected void onPreExecute(){
            showProgressBar(true);
            isLoading=true;
        }

        @Override
        protected List<Movie> doInBackground(Void... voids) {
            //Get Movies
            return serviceClient.getListMovies(numPage);
        }

        @Override
        protected void onPostExecute(List<Movie> list){
            if(list!=null && list.size()>0){
                adapter.setMovies(list);

                if(charSequence!=null && charSequence!=""){
                    adapter.getFilter().filter(charSequence);
                }

                numPage += 1;
            }
            showProgressBar(false);
            isLoading = false;
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
        adapter = new RecyclerViewAdapter(new ArrayList<Movie>(), getApplicationContext());
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
}
