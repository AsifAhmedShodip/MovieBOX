package com.example.asif.movies.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.asif.movies.DetailActivity;
import com.example.asif.movies.MainPage;
import com.example.asif.movies.R;
import com.example.asif.movies.model.Movie;

import java.util.List;

import static com.example.asif.movies.MainActivity.movieStatic;

/**
 * Created by asif on 30-Mar-18.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder>{

    private Context mContext;
    private List<Movie> movieList;

    public MoviesAdapter(Context mContext,List<Movie> movieList){
        this.mContext = mContext;
        this.movieList = movieList;
    }
    @Override
    public MoviesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.movie_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviesAdapter.MyViewHolder viewHolder, final int i) {

        String poster = "https://image.tmdb.org/t/p/w500" + movieList.get(i).getPosterPath();

        Glide.with(mContext)
                .load(poster)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.load)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(viewHolder.thumbnail);

        viewHolder.thumbnail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                    Movie clickedDataItem = movieList.get(i);
                    movieStatic = clickedDataItem;
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    ((Activity)mContext).overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, userrating;
        public ImageView thumbnail;

        public MyViewHolder(View view){
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

        }
    }
}
