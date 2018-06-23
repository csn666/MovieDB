package com.example.csanz.moviedb.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.csanz.moviedb.Data.Movie;
import com.example.csanz.moviedb.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.csanz.moviedb.Data.Constants.URL_IMAGES;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MovieViewHolder> {

    private List<Movie> movies = new ArrayList<>();
    private Context context;

    public RecyclerViewAdapter(List<Movie> list, Context mContext){
        this.movies = list;
        this.context = mContext;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if(movies!=null && movies.size()>0) {
            Movie movie = movies.get(position);
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
    }

    @Override
    public int getItemCount() {
        return this.movies.size();
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
}
