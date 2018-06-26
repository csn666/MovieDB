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
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.csanz.moviedb.Adapter.RecyclerViewAdapter;
import com.example.csanz.moviedb.Data.Movie;
import com.example.csanz.moviedb.Syn.ServiceClientImpl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.newSearch {

    @BindView(R.id.recyclerViewMovies)
    RecyclerView listMovies;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.editSearch)
    EditText editSearch;
    @BindView(R.id.imgClearSearch)
    ImageView btnClearSearch;

    private int numPage=1;
    private ServiceClientImpl serviceClient;
    private RecyclerViewAdapter adapter;
    CharSequence charSequence=null;

    boolean loading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Connect to api TMDB
        serviceClient = new ServiceClientImpl();
        serviceClient.connectAPI();

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
                //If editText is not empty, hide button clear.
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

        //Setup adapter
        setupAdapter();
        //Execute async task to get movies from TMDB
        new showMovies().execute();

        //Add scrolllistener to recyclerview to detect the end of this
        listMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int visibleItemCount=0;
            int totalItemCount=0;
            int pastVisibleItems=0;
            int currentScrollState = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                currentScrollState = newState;

                if(pastVisibleItems+visibleItemCount >= totalItemCount && currentScrollState==0){
                    if(!loading){
                        loading = true;
                        new showMovies().execute();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView,dx,dy);
                LinearLayoutManager mLayoutManager = (LinearLayoutManager)listMovies.getLayoutManager();
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
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

                //Apply filter.
                if(charSequence!=null && charSequence!=""){
                    adapter.getFilter().filter(charSequence);
                }else{ // if not search by filter, return all movies
                    adapter.getFilter().filter("");
                }

                //Increment numPage to obtain in next charge new movies
                numPage += 1;
            }

            loading = false;

            showProgressBar(false);
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
        adapter = new RecyclerViewAdapter( new ArrayList<Movie>(),MainActivity.this);
        this.listMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.listMovies.setAdapter(adapter);
    }


    /*
     * Method that it is called from adapter when result searched don't return matches in the actual page.
     * This method is called until found matches.
     */
    public void newSearch(){
        new showMovies().execute();
    }
}
