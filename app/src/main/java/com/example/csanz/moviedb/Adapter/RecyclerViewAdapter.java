package com.example.csanz.moviedb.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.csanz.moviedb.Data.Movie;
import com.example.csanz.moviedb.MainActivity;
import com.example.csanz.moviedb.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.csanz.moviedb.Data.Constants.URL_IMAGES;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MovieViewHolder> implements Filterable {

    private List<Movie> movies;
    private List<Movie> moviesFiltered;
    private Context context;
    private int totalItemsSerched=0;
    private int auxTotalItems = 0;

    public RecyclerViewAdapter(List<Movie> mList, Context mContext){
        movies = mList;
        moviesFiltered = mList;
        context = mContext;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = moviesFiltered.get(position);
        holder.title.setText(movie.getTitle());
        //get year
        holder.date.setText(movie.getReleaseDate().substring(0, 4));
        holder.description.setText(movie.getOverview());
        //use picasso to get poster movie
        String urlImage = URL_IMAGES + movie.getPosterPath();
        Picasso.with(context).cancelRequest(holder.picture);
        Picasso.with(context)
                .load(urlImage)
                .into(holder.picture);
    }

    @Override
    public int getItemCount() {
        return moviesFiltered.size();
    }

    public void setMovies(List<Movie> list){
        movies.addAll(list);
        //Log.d("TAG Add movies", String.valueOf(list.size())+ " movies added");
        //moviesFiltered = movies;
        //notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                List<Movie> filtered = new ArrayList<>();

                if(charString.isEmpty()){
                    filtered = movies;
                }else{
                    for(Movie m : movies){
                        if(m.getTitle().toLowerCase().contains(charString)){
                            filtered.add(m);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.count = filtered.size();
                filterResults.values = filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                moviesFiltered = (ArrayList<Movie>)results.values;
                if(moviesFiltered.size()==totalItemsSerched){
                    ((MainActivity)context).newSearch();
                }else{
                    totalItemsSerched = moviesFiltered.size();
                    notifyDataSetChanged();
                }
            }
        };
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.txtTitulo)
        TextView title;
        @BindView(R.id.txtFecha)
        TextView date;
        @BindView(R.id.txtOverview)
        TextView description;
        @BindView(R.id.imgPortada)
        ImageView picture;



        public MovieViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public interface newSearch{
        void newSearch();
    }
}
