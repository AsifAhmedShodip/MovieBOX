package com.example.asif.movies;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.asif.movies.WebView_Link.BrowserWebview;
import com.example.asif.movies.api.Client;
import com.example.asif.movies.api.Service;
import com.example.asif.movies.model.AccountStates;
import com.example.asif.movies.model.CheckItemStatus;
import com.example.asif.movies.model.Genre;
import com.example.asif.movies.model.ListResponse;
import com.example.asif.movies.model.MediaID;
import com.example.asif.movies.model.Movie;
import com.example.asif.movies.model.OmdbMovieResponse;
import com.example.asif.movies.model.WatchListBody;
import com.example.asif.movies.model.WatchListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.asif.movies.LogIn.session_id;
import static com.example.asif.movies.MainActivity.movieStatic;

/**
 * Created by asif on 30-Mar-18.
 */

public class DetailActivity extends AppCompatActivity implements View.OnClickListener{
    TextView nameOfMovie, plotSynopsis, userRating, releaseDate,imdbRating,wiki,imdbLink;
    ImageView imageView;

    String thumbnail;
    String movieDirector;
    String year;
    Integer runtime;
    String movieName;
    String synopsis;
    String rating;
    String dateOfRelease;
    String poster;
    TextView genres;
    int movie_id;
    private FloatingActionButton floatingActionButton;
    Movie movie;

    private AccountStates accountStates;
    private boolean watchlistStatus,watchedMovieStatus,plot = false;
    private int watchedMoviesListID;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        movie = movieStatic;
        getSupportActionBar().setTitle(movie.getTitle());
        initCollapsingToolbar();
        checkWatchListStatus();
        checkWatchedMoviesStatus();

        floatingActionButton = findViewById(R.id.fab);
        imageView = (ImageView) findViewById(R.id.thumbnail_image_header);
        //picture = findViewById(R.id.picture);
        nameOfMovie = (TextView) findViewById(R.id.title);
        genres = (TextView) findViewById(R.id.genre);
        plotSynopsis = (TextView) findViewById(R.id.plotsynopsis);
        releaseDate = (TextView) findViewById(R.id.releasedate);
        imdbRating = findViewById(R.id.imdbrating);
        setTextViewDrawableColor(imdbRating, R.color.ratingColor);
        wiki = findViewById(R.id.wiki);
        setTextViewDrawableColor(wiki,R.color.colorAccent);
        imdbLink = findViewById(R.id.imdblink);

        thumbnail = (String) movie.getBackdropPath(); //Change
        movieName = movie.getOriginalTitle();
        movieDirector = "Ryan Gosling";
        synopsis = movie.getOverview();
        rating = Double.toString(movie.getVoteAverage());
        dateOfRelease = movie.getReleaseDate();
        movie_id = movie.getId();
        poster = (String) movie.getPosterPath();
        runtime = movie.getRuntime();

        final String poster01 = "https://image.tmdb.org/t/p/w500" + thumbnail;

        Glide.with(this)
                .load(poster01)
                .placeholder(R.drawable.load)
                .into(imageView);
        plotSynopsis.setText(synopsis);

        String [] dateParts = movie.getReleaseDate().split("-");
        year = dateParts[0];

        aquireRuntime();
        plotSynopsis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!plot) {
                    plotSynopsis.setMaxLines(Integer.MAX_VALUE);
                    plot = true;
                }
                else {
                    plotSynopsis.setMaxLines(3);
                    plot = false;
                }
            }
        });

        //Setting IMDB URL
        imdbLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoIMDB();
            }
        });

        //Setting Wikipedia
        wiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://en.wikipedia.org/wiki/";
                String movieName = movie.getTitle().trim();
                movieName.replace(" ","_");
                Intent intent = new Intent(getApplicationContext(),BrowserWebview.class);
                intent.putExtra("url",url+movieName);
                intent.putExtra("movie name" , movieName);
                startActivity(intent);
            }
        });

        //Setting IMDB Rating
        setImdbRating();

        floatingActionButton.setOnClickListener(this);
    }

    private void setImdbRating() {
        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<Movie> call = apiService.getMovieDetails(movie.getId(),BuildConfig.THE_MOVIE_DB_API_TOKEN);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
               callOMDB(response.body().getImdbId());
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(DetailActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void callOMDB(String imdbId) {
        Client Client = new Client();
        Service apiService = Client.getClientOmdb().create(Service.class);
        Call<OmdbMovieResponse> call = apiService.getImdbRating(BuildConfig.THE_OMDB_API,imdbId);
        call.enqueue(new Callback<OmdbMovieResponse>() {
            @Override
            public void onResponse(Call<OmdbMovieResponse> call, Response<OmdbMovieResponse> response) {
                imdbRating.setText(response.body().getImdbRating());
                Log.d("Error", "response.raw().request().url();"+response.raw().request().url());
            }

            @Override
            public void onFailure(Call<OmdbMovieResponse> call, Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(DetailActivity.this, "Error Fetching Data from OMDB", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void gotoIMDB() {
        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<Movie> call = apiService.getMovieDetails(movie.getId(),BuildConfig.THE_MOVIE_DB_API_TOKEN);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                movie = response.body();
                String url = "http://www.imdb.com/title/"+response.body().getImdbId()+"/";
                Intent intent = new Intent(getApplicationContext(),BrowserWebview.class);
                intent.putExtra("url",url);
                intent.putExtra("imdb" , "imdb");
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(DetailActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void aquireRuntime() {
        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<Movie> call = apiService.getMovieDetails(movie.getId(),BuildConfig.THE_MOVIE_DB_API_TOKEN);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if(response.body().getRuntime()!=null){
                    int time = response.body().getRuntime();
                    int hours = time / 60; //since both are ints, you get an int
                    int minutes = time % 60;
                    releaseDate.setText(year+"  .  "+hours+"h "+minutes+"m");
                    setMovieGenre(response.body().getGenres());
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(DetailActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setMovieGenre(List<Genre> genre) {
        String g = null;
        for(int i =0; i< genre.size();i++)
        {
            if(i == 0)
                g = genre.get(i).getName();
            else
            {
                g = g + " . "+genre.get(i).getName();
            }
        }

        genres.setText(g);
    }

    private void checkWatchedMoviesStatus() {
        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<ListResponse> call = apiService.getListResponse(BuildConfig.THE_MOVIE_DB_API_TOKEN,session_id);
        call.enqueue(new Callback<ListResponse>() {
            @Override
            public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                ListResponse listResponse = response.body();
                for(int i=0;i<listResponse.getList().size();i++)
                {
                    if(listResponse.getList().get(i).getName().equals("Watched Movies"))
                    {
                        watchedMoviesListID = listResponse.getList().get(i).getId();
                        Client Client = new Client();
                        Service apiService = Client.getClient().create(Service.class);
                        Call<CheckItemStatus> call01 = apiService.checkItemStatus(listResponse.getList().get(i).getId(),
                                BuildConfig.THE_MOVIE_DB_API_TOKEN, movie.getId());
                        call01.enqueue(new Callback<CheckItemStatus>() {
                            @Override
                            public void onResponse(Call<CheckItemStatus> call, Response<CheckItemStatus> response) {
                                watchedMovieStatus = response.body().getItemPresent();
                            }
                            @Override
                            public void onFailure(Call<CheckItemStatus> call, Throwable t) {
                                Log.d("Error", call.request().url().toString());
                                Toast.makeText(DetailActivity.this,"Error States",  Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ListResponse> call, Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(DetailActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkWatchListStatus() {
        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<AccountStates> call = apiService.getAccountStates( movie.getId(),BuildConfig.THE_MOVIE_DB_API_TOKEN, session_id);
        call.enqueue(new Callback<AccountStates>() {
            @Override
            public void onResponse(Call<AccountStates> call, Response<AccountStates> response) {
                watchlistStatus = response.body().getWatchlist();
            }
            @Override
            public void onFailure(Call<AccountStates> call, Throwable t) {
                Log.d("Error", call.request().url().toString());
                Toast.makeText(DetailActivity.this,"Error States",  Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(),
                        color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private void initCollapsingToolbar(){
        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(movieName);
        collapsingToolbarLayout.setTitleEnabled(true);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener(){
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset){
                if (scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0){
                    collapsingToolbarLayout.setTitle(movieName);
                    isShow = true;
                }else if (isShow){
                    collapsingToolbarLayout.setTitle(movieName);
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                final DialogMenu dialogMenu = new DialogMenu(this);
                gettingReadyForDialogMenu(dialogMenu);
                dialogMenu.setListener(new DialogMenu.OnDialogMenuListener() {
                    @Override
                    public void onWatchedPress() {
                        afterWatchedPressed(dialogMenu);
                    }

                    @Override
                    public void onWishPress() {
                      afterWishPressed(dialogMenu);
                    }

                    @Override
                    public void onListPress() {
                        Toast.makeText(DetailActivity.this, "LIST", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    private void afterWatchedPressed(DialogMenu dialogMenu) {
        if(!watchedMovieStatus){
            watchedMovieStatus = true;
            setTextViewDrawableColor(dialogMenu.watched, R.color.colorAccent);
            Client Client = new Client();
            MediaID mediaID = new MediaID();
            mediaID.setMediaId(movie_id);
            Service apiService = Client.getClient().create(Service.class);
            Call<WatchListResponse> call = apiService.addWatchedMovies(watchedMoviesListID,BuildConfig.THE_MOVIE_DB_API_TOKEN,
                    session_id,mediaID);
            call.enqueue(new Callback<WatchListResponse>() {
                @Override
                public void onResponse(Call<WatchListResponse> call, Response<WatchListResponse> response) {
                    Log.d("Error02", response.body().getStatusCode()+" "+
                            response.body().getStatusMessage());
                }

                @Override
                public void onFailure(Call<WatchListResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(DetailActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }
        else{
            watchedMovieStatus = false;
            setTextViewDrawableColor(dialogMenu.watched, R.color.black);
            Client Client = new Client();
            MediaID mediaID = new MediaID();
            mediaID.setMediaId(movie_id);
            Service apiService = Client.getClient().create(Service.class);
            Call<WatchListResponse> call = apiService.deleteWatchedMovies(watchedMoviesListID,BuildConfig.THE_MOVIE_DB_API_TOKEN,
                    session_id,mediaID);
            call.enqueue(new Callback<WatchListResponse>() {
                @Override
                public void onResponse(Call<WatchListResponse> call, Response<WatchListResponse> response) {
                    Log.d("Error03", response.body().getStatusCode()+" "+
                            response.body().getStatusMessage());
                }

                @Override
                public void onFailure(Call<WatchListResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(DetailActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void gettingReadyForDialogMenu(DialogMenu dialogMenu) {
        setTextViewDrawableColor(dialogMenu.wish, R.color.black);
        setTextViewDrawableColor(dialogMenu.watched, R.color.black);
        setTextViewDrawableColor(dialogMenu.list, R.color.black);
        if(watchlistStatus) {
            setTextViewDrawableColor(dialogMenu.wish, R.color.colorAccent);
        }
        if(watchedMovieStatus){
            setTextViewDrawableColor(dialogMenu.watched, R.color.colorAccent);
        }
        dialogMenu.name.setText(movie.getTitle()+"\n"+year);
    }

    private void afterWishPressed(final DialogMenu dialogMenu) {
        final WatchListBody watchListBody = new WatchListBody();
        watchListBody.setMediaId(movie_id);
        watchListBody.setMediaType("movie");
        if(watchlistStatus)
            watchListBody.setWatchlist(false);
        else
            watchListBody.setWatchlist(true);

        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<WatchListResponse> call = apiService.postWatchList(BuildConfig.THE_MOVIE_DB_API_TOKEN, session_id,watchListBody);
        call.enqueue(new Callback<WatchListResponse>() {
            @Override
            public void onResponse(Call<WatchListResponse> call, Response<WatchListResponse> response) {
                Integer status = response.body().getStatusCode();
                if(status == 1)
                {
                    setTextViewDrawableColor(dialogMenu.wish, R.color.colorAccent);
                    watchlistStatus = true;
                }
                else if(status == 12)
                {
                    setTextViewDrawableColor(dialogMenu.wish, R.color.colorAccent);
                    watchlistStatus=true;
                }
                else if(status == 13){
                    setTextViewDrawableColor(dialogMenu.wish, R.color.black);
                    watchlistStatus=false;
                }
            }

            @Override
            public void onFailure(Call<WatchListResponse> call, Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(DetailActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
