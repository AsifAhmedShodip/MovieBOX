package com.example.asif.movies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.asif.movies.DetailActivityForCoverPhoto;
import com.example.asif.movies.R;
import com.example.asif.movies.model.Movie;

import java.util.List;

import static com.example.asif.movies.MainActivity.movieStatic;

/**
 * Created by asif on 07-Apr-18.
 */

public class MoviesAdapterForCoverPhotoSearch extends RecyclerView.Adapter<MoviesAdapterForCoverPhotoSearch.MyViewHolder>{
    private Context mContext;
    private List<Movie> movieList;

    public MoviesAdapterForCoverPhotoSearch(Context mContext, List<Movie> movieList){
        this.mContext = mContext;
        this.movieList = movieList;
    }
    @Override
    public MoviesAdapterForCoverPhotoSearch.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.movie_card, parent, false);
        return new MoviesAdapterForCoverPhotoSearch.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviesAdapterForCoverPhotoSearch.MyViewHolder viewHolder, int i) {

        String poster = "https://image.tmdb.org/t/p/w500" + movieList.get(i).getPosterPath();

        Glide.with(mContext)
                .load(poster)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.load)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(viewHolder.thumbnail);

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

            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        Movie clickedDataItem = movieList.get(pos);
                        movieStatic = clickedDataItem;
                        Intent intent = new Intent(mContext, DetailActivityForCoverPhoto.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("Movie Id",clickedDataItem.getId());
                        mContext.startActivity(intent);
                    }
                }
            });

        }
    }
}

