package com.example.asif.movies.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.asif.movies.BrowseMovies.NowPlayingMovies;
import com.example.asif.movies.BuildConfig;
import com.example.asif.movies.DetailActivity;
import com.example.asif.movies.Dialouge.DialogMenu;
import com.example.asif.movies.Dialouge.Long_pressed_dialouge;
import com.example.asif.movies.R;
import com.example.asif.movies.WebView_Link.BrowserWebview;
import com.example.asif.movies.api.Client;
import com.example.asif.movies.api.Service;
import com.example.asif.movies.model.Account.AccountDetails;
import com.example.asif.movies.model.AccountStates;
import com.example.asif.movies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import com.example.asif.movies.BrowseMovies.BrowseMovies;
import com.example.asif.movies.model.OmdbMovieResponse;
import com.example.asif.movies.starting.StartUpActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.asif.movies.starting.StartUpActivity.seenMovies;

/**
 * Created by asif on 30-Mar-18.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder>{

    String movieCount;
    boolean watchedMovieStatus = false;
    final String[] totalCount = new String[1];

    private Context mContext;
    private List<Movie> movieList;

    public MoviesAdapter(Context mContext,List<Movie> movieList){
        this.mContext = mContext;
        this.movieList = movieList;
    }

    public List<Movie> getMovies() {
        return movieList;
    }

    public void setMovies(List<Movie> movieList) {
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
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v){
                    Movie clickedDataItem = movieList.get(i);
                    BrowseMovies.movieStatic = clickedDataItem;
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext,viewHolder.thumbnail,"poster");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent,optionsCompat.toBundle());
                    //((Activity)mContext).overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
            }
        });

        viewHolder.thumbnail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Long_pressed_dialouge Long_pressed_dialouge = new Long_pressed_dialouge(mContext);
                checkWatchedMoviesStatus(movieList.get(i).getId(),Long_pressed_dialouge);
                Long_pressed_dialouge.name.setText(movieList.get(i).getTitle());
                Long_pressed_dialouge.year.setText(movieList.get(i).getReleaseDate().split("-")[0]);
                Long_pressed_dialouge.tmdb.setText(movieList.get(i).getVoteAverage().toString());
                getImdbId(movieList.get(i).getId(),Long_pressed_dialouge);
                checkWatchListStatus(movieList.get(i).getId(),Long_pressed_dialouge);

                Long_pressed_dialouge.setListener(new Long_pressed_dialouge.OnDialogMenuListener() {
                    @Override
                    public void onWatchedPress() {
                        afterWatchedPressed(Long_pressed_dialouge);
                        NowPlayingMovies.adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onWishedPress() {

                    }
                });
                return false;
            }
        });

        if(seenMovies.contains(movieList.get(i).getId().toString())){
            viewHolder.seen.setVisibility(View.VISIBLE);
        }
        else if(!seenMovies.contains(movieList.get(i).getId().toString())){
            viewHolder.seen.setVisibility(View.INVISIBLE);
        }

        final DatabaseReference databaseUsers3= FirebaseDatabase.getInstance().getReference().child("Total Time");
        databaseUsers3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot users : dataSnapshot.getChildren())
                {
                    if(users.getKey().equals(AccountDetails.getCurrentUser().getUsername())) {
                        totalCount[0] = users.getValue(String.class);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getImdbId(Integer id,final Long_pressed_dialouge dialouge) {
        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<Movie> call = apiService.getMovieDetails(id,BuildConfig.THE_MOVIE_DB_API_TOKEN);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie.setCurrentMovieInLongPressed(response.body());
                setImdbRating(response.body().getImdbId(),dialouge);
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(mContext, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, userrating;
        ImageButton seen;
        public ImageView thumbnail;

        public MyViewHolder(View view){
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            seen = view.findViewById(R.id.beenhere);

           // seen.setVisibility(View.GONE);
        }
    }

    private void checkWatchedMoviesStatus(final int movieID , final Long_pressed_dialouge Long_pressed_dialouge) { // implemented in firebase
        final DatabaseReference databaseUsers= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(AccountDetails.getCurrentUser().getUsername());
        final boolean[] flag = {false};
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot users : dataSnapshot.getChildren())
                {
                    if(users.getKey().equals(String.valueOf(movieID))) {
                        movieCount = users.getValue(String.class);
                        watchedMovieStatus = true;
                        if(!movieCount.equals("0")) {
                            Long_pressed_dialouge.watched.setText("x" + movieCount);
                            setTextViewDrawableColor(Long_pressed_dialouge.watched, R.color.colorAccent);
                            flag[0] = true;
                        }
                        else {
                            Long_pressed_dialouge.watched.setText("Watched");
                            setTextViewDrawableColor(Long_pressed_dialouge.watched, R.color.white);
                        }
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext,
                        color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private void setImdbRating(String imdbId, final Long_pressed_dialouge dialouge) {
        Client Client = new Client();
        Service apiService = Client.getClientOmdb().create(Service.class);
        Call<OmdbMovieResponse> call = apiService.getImdbRating(BuildConfig.THE_OMDB_API,imdbId
        );
        call.enqueue(new Callback<OmdbMovieResponse>() {
            @Override
            public void onResponse(Call<OmdbMovieResponse> call, Response<OmdbMovieResponse> response) {
                Log.d("Error", "response.raw().request().url();"+response.raw().request().url());
                boolean flag = false;
                dialouge.imdb.setText(response.body().getImdbRating());
                dialouge.meta.setText(response.body().getMetascore());
                for(int i= 0 ;i<response.body().getRatings().size();i++) {
                    if (response.body().getRatings().get(i).getSource().equals("Rotten Tomatoes")) {
                        dialouge.rotten.setText(response.body().getRatings().get(i).getValue());
                        flag = true;
                    }
                }
                if(!flag){
                    dialouge.rotten.setText("N/A");
                }
            }

            @Override
            public void onFailure(Call<OmdbMovieResponse> call, Throwable t) {
                //Log.d("Error", t.getMessage());
                Toast.makeText(mContext, "Error Fetching Data from OMDB", Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void checkWatchListStatus(int id,final Long_pressed_dialouge Long_pressed_dialouge) {
        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        SharedPreferences sharedpreferences = mContext.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String session_id = sharedpreferences.getString("Session_id","");
        Call<AccountStates> call = apiService.getAccountStates(id,BuildConfig.THE_MOVIE_DB_API_TOKEN, session_id);
        call.enqueue(new Callback<AccountStates>() {
            @Override
            public void onResponse(Call<AccountStates> call, Response<AccountStates> response) {
                boolean watchlistStatus = response.body().getWatchlist();
                if(watchlistStatus)
                    setTextViewDrawableColor(Long_pressed_dialouge.wished, R.color.colorAccent);
                else
                    setTextViewDrawableColor(Long_pressed_dialouge.wished, R.color.white);

                Log.d("Errorhahaha", "response.raw().request().url()"+response.raw().request().url());
            }
            @Override
            public void onFailure(Call<AccountStates> call, Throwable t) {
                Log.d("Errorhahaha", call.request().url().toString());
                Toast.makeText(mContext,"Error States",  Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void afterWatchedPressed(Long_pressed_dialouge long_pressed_dialouge) {
        final int[] count = {Integer.parseInt(movieCount)};
        final int[] time = new int[1];
        final String[] temp = new String[1];
        final DatabaseReference databaseUsers= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(AccountDetails.getCurrentUser().getUsername());
        final DatabaseReference databaseUsers2= FirebaseDatabase.getInstance().getReference().child("Total Time")
                .child(AccountDetails.getCurrentUser().getUsername());

        if(!movieCount.equals("0")){
            DialogMenu dialogMenu = new DialogMenu(mContext);
            dialogMenu.setListener(new DialogMenu.OnDialogMenuListener() {
                @Override
                public void onReWatchedPress() {
                    count[0]++;
                    temp[0] = String.valueOf(count[0]);
                    time[0] = Integer.parseInt(totalCount[0]) + Movie.getCurrentMovieInLongPressed().getRuntime();
                    databaseUsers.child(String.valueOf(Movie.getCurrentMovieInLongPressed().getId())).setValue(temp[0]);
                    databaseUsers2.setValue(String.valueOf(time[0]));
                    if(!StartUpActivity.seenMovies.contains(Movie.getCurrentMovieInLongPressed().getId().toString()))
                        StartUpActivity.seenMovies.add(Movie.getCurrentMovieInLongPressed().getId().toString());
                }

                @Override
                public void onNotWatchedPress() {
                    count[0]--;
                    temp[0] = String.valueOf(count[0]);
                    time[0] = Integer.parseInt(totalCount[0]) - Movie.getCurrentMovieInLongPressed().getRuntime();
                    databaseUsers.child(String.valueOf(Movie.getCurrentMovieInLongPressed().getId())).setValue(temp[0]);
                    databaseUsers2.setValue(String.valueOf(time[0]));
                    if(!String.valueOf(time[0]).equals("0")) {//not working
                        StartUpActivity.seenMovies.remove(Movie.getCurrentMovieInLongPressed().getId().toString());
                    }
                }
            });
        }
        else{
            count[0]++;
            temp[0] = String.valueOf(count[0]);
            setTextViewDrawableColor(long_pressed_dialouge.watched, R.color.colorAccent);
            time[0] = Integer.parseInt(totalCount[0]) + Movie.getCurrentMovieInLongPressed().getRuntime();
            databaseUsers.child(String.valueOf(Movie.getCurrentMovieInLongPressed().getId())).setValue(temp[0]);
            databaseUsers2.setValue(String.valueOf(time[0]));
            if(!StartUpActivity.seenMovies.contains(Movie.getCurrentMovieInLongPressed().getId().toString()))
                StartUpActivity.seenMovies.add(Movie.getCurrentMovieInLongPressed().getId().toString());
        }
    }
}
