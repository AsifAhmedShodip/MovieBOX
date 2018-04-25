package com.example.asif.movies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.asif.movies.Dialouge.DialogMenu;
import com.example.asif.movies.Profile.DetailActivityForCoverPhoto;
import com.example.asif.movies.WebView_Link.BrowserWebview;
import com.example.asif.movies.WebView_Link.SignUpWebview;
import com.example.asif.movies.adapter.CastAdapter;
import com.example.asif.movies.adapter.MoviesAdapter;
import com.example.asif.movies.api.Client;
import com.example.asif.movies.api.Service;
import com.example.asif.movies.model.Account.AccountDetails;
import com.example.asif.movies.model.AccountStates;
import com.example.asif.movies.model.Cast_crew.CastResponse;
import com.example.asif.movies.model.Genre;
import com.example.asif.movies.model.Movie;
import com.example.asif.movies.model.MovieVideo;
import com.example.asif.movies.model.MovieVideoResponse;
import com.example.asif.movies.model.MoviesResponse;
import com.example.asif.movies.model.OmdbMovieResponse;
import com.example.asif.movies.model.WatchListBody;
import com.example.asif.movies.model.WatchListResponse;
import com.example.asif.movies.starting.StartUpActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.asif.movies.BrowseMovies.BrowseMovies.movieStatic;

/**
 * Created by asif on 30-Mar-18.
 */

public class DetailActivity extends AppCompatActivity implements View.OnClickListener{
    TextView nameOfMovie, plotSynopsis, userRating, releaseYear,imdbRating,wiki,imdbLink ,trailer,director;
    ImageView imageView , poster;
    TextView genres,watched , list , wish;
    Movie movie,movieDetails;
    String movieCount = "0",totalCount = "0";
    CoordinatorLayout screen;
    String session_id;
    RecyclerView recyclerView_cast ,recyclerView_recom;
    CastAdapter castAdapter ,castAdapter2;
    MoviesAdapter moviesAdapter;
    String directorName;
    ShimmerFrameLayout shimmerFrameLayout;
    private ProgressDialog progress;
    private boolean watchlistStatus, watchedMovieStatus, plot = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        supportPostponeEnterTransition();
        StatusBarUtil.setTransparent(DetailActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        movie = movieStatic;
        getSupportActionBar().setTitle(movie.getTitle());
        screen = findViewById(R.id.main_content);
        initCollapsingToolbar();

        imageView = (ImageView) findViewById(R.id.thumbnail_image_header);
        director = findViewById(R.id.director);
        poster = (ImageView) findViewById(R.id.poster);
        nameOfMovie = (TextView) findViewById(R.id.title);
        genres = (TextView) findViewById(R.id.genre);
        plotSynopsis = (TextView) findViewById(R.id.plotsynopsis);
        releaseYear = (TextView) findViewById(R.id.releasedate);
        imdbRating = findViewById(R.id.imdbrating);
        setTextViewDrawableColor(imdbRating, R.color.ratingColor);
        wiki = findViewById(R.id.wiki);
        imdbLink = findViewById(R.id.imdblink);
        watched = (TextView) findViewById(R.id.watched);
        wish = (TextView) findViewById(R.id.wish);
        list = (TextView) findViewById(R.id.list);
        trailer = findViewById(R.id.trailer);
        recyclerView_cast = findViewById(R.id.recycler_cast);
        recyclerView_recom = findViewById(R.id.recycler_recom);
        shimmerFrameLayout = findViewById(R.id.shimmer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            poster.setTransitionName(movie.getId().toString());
            setPoster(movie.getPosterPath());
        }

        shimmerFrameLayout.startShimmerAnimation();


        progress=new ProgressDialog(this);
        progress.setMessage("Loading ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        //progress.show();

        setTextViewDrawableColor(wish, R.color.black);
        setTextViewDrawableColor(watched, R.color.black);
        setTextViewDrawableColor(list, R.color.black);

        SharedPreferences sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        session_id = sharedpreferences.getString("Session_id","");

        checkWatchedMoviesStatus();
        checkWatchListStatus();
        settingUpDetailsForMovie();
        countTotalTime();
        getTrailer();

        plotSynopsis.setOnClickListener(this);
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

        trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.youtube.com/watch?v="+MovieVideo.getMovieVideo().getKey();
                Log.d("url   ** ", url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        watched.setOnClickListener(this);
        wish.setOnClickListener(this);
        list.setOnClickListener(this);
        imageView.setOnClickListener(this);
        director.setOnClickListener(this);
    }

    private void getTrailer() {
        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<MovieVideoResponse> call = apiService.getMovieVideoResponse(movie.getId(),BuildConfig.THE_MOVIE_DB_API_TOKEN);
        call.enqueue(new Callback<MovieVideoResponse>() {
            @Override
            public void onResponse(Call<MovieVideoResponse> call, Response<MovieVideoResponse> response) {
                List<MovieVideo> movieVideos = response.body().getResults();
                for(int i = 0 ;i< movieVideos.size();i++){
                    if(movieVideos.get(i).getSite().equals("YouTube")){
                        MovieVideo.setMovieVideo(movieVideos.get(i));
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieVideoResponse> call, Throwable t) {
                progress.dismiss();
                Log.d("Error", t.getMessage());
                Toast.makeText(DetailActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void countTotalTime() {
        final DatabaseReference databaseUsers= FirebaseDatabase.getInstance().getReference().child("Total Time");
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot users : dataSnapshot.getChildren())
                {
                    if(users.getKey().equals(AccountDetails.getCurrentUser().getUsername()))
                        totalCount = users.getValue(String.class);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void settingUpDetailsForMovie() {

        setBackdrop(movie.getBackdropPath());
        setMovieGenre(movie.getGenres());
        //setPoster(movie.getPosterPath());
        plotSynopsis.setText(movie.getOverview());

        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<Movie> call = apiService.getMovieDetails(movie.getId(),BuildConfig.THE_MOVIE_DB_API_TOKEN);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                movieDetails = response.body();
                //setMovieGenre(response.body().getGenres());
                if(response.body().getRuntime()!= null)// have to modify
                    setMovieYearAndRuntime(response.body().getReleaseDate(),response.body().getRuntime());
                //plotSynopsis.setText(response.body().getOverview());
                //setBackdrop(response.body().getBackdropPath());
                //setPoster(response.body().getPosterPath());
                setImdbRating(response.body().getImdbId());
                setCast(response.body().getId());
                setRecomendations(response.body().getId());
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                progress.dismiss();
                Log.d("Error", t.getMessage());
                Toast.makeText(DetailActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setRecomendations(Integer id) {
        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<MoviesResponse> call = apiService.getRecomendations(id,BuildConfig.THE_MOVIE_DB_API_TOKEN,"en-US",1);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                moviesAdapter = new MoviesAdapter(DetailActivity.this, response.body().getMovies());
                recyclerView_recom.smoothScrollToPosition(0);
                recyclerView_recom.setLayoutManager(new LinearLayoutManager(DetailActivity.this,LinearLayoutManager.HORIZONTAL,false));
                recyclerView_recom.setItemAnimator(new DefaultItemAnimator());
                recyclerView_recom.setAdapter(moviesAdapter);
                moviesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                progress.dismiss();
                Log.d("Error", t.getMessage());
                Toast.makeText(DetailActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setCast(Integer id) {
        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<CastResponse> call = apiService.getCastDetails(id,BuildConfig.THE_MOVIE_DB_API_TOKEN);
        call.enqueue(new Callback<CastResponse>() {
            @Override
            public void onResponse(Call<CastResponse> call, Response<CastResponse> response) {
                for(int i = 0; i<response.body().getCrew().size();i++){
                    if(response.body().getCrew().get(i).getJob().equals("Director")){
                        directorName = response.body().getCrew().get(i).getName();
                        String name = "<b>"+directorName+"</b>";
                        director.append(Html.fromHtml(name));
                        break;
                    }
                }
                castAdapter = new CastAdapter(DetailActivity.this, response.body().getCast());
                recyclerView_cast.smoothScrollToPosition(0);
                recyclerView_cast.setLayoutManager(new LinearLayoutManager(DetailActivity.this,LinearLayoutManager.HORIZONTAL,false));
                recyclerView_cast.setItemAnimator(new DefaultItemAnimator());
                recyclerView_cast.setAdapter(castAdapter);
                castAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<CastResponse> call, Throwable t) {
                progress.dismiss();
                Log.d("Error", t.getMessage());
                Toast.makeText(DetailActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setPoster(Object posterPath) {
        final String path = "https://image.tmdb.org/t/p/w500" + posterPath;
        Glide.with(this)
                .load(path)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.load)
                        .centerCrop())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(poster);

        /*supportPostponeEnterTransition();

        Glide.with(this)
                .load(path)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.load)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(poster);*/
    }

    private void setBackdrop(String backdropPath) {
        final String poster = "https://image.tmdb.org/t/p/original" + backdropPath;
        Glide.with(this)
                .load(poster)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.loading)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(imageView);
    }

    private void setMovieYearAndRuntime(String releaseDate,int runtime) {
        String [] dateParts = releaseDate.split("-");
        String year = dateParts[0];
        if(runtime != 0){
            int time = runtime;
            int hours = time / 60;
            int minutes = time % 60;
            releaseYear.setText(year+"  .  "+hours+"h "+minutes+"m");
        }
    }

    private void setImdbRating(String imdbId) {
        Client Client = new Client();
        Service apiService = Client.getClientOmdb().create(Service.class);
        Call<OmdbMovieResponse> call = apiService.getImdbRating(BuildConfig.THE_OMDB_API,imdbId);
        call.enqueue(new Callback<OmdbMovieResponse>() {
            @Override
            public void onResponse(Call<OmdbMovieResponse> call, Response<OmdbMovieResponse> response) {
                imdbRating.setText(response.body().getImdbRating());
                shimmerFrameLayout.stopShimmerAnimation();
                director.setBackground(null);
                Log.d("Error", "response.raw().request().url();"+response.raw().request().url());
            }

            @Override
            public void onFailure(Call<OmdbMovieResponse> call, Throwable t) {
                //Log.d("Error", t.getMessage());
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

    private void checkWatchedMoviesStatus() { // implemented in firebase
        final DatabaseReference databaseUsers= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(AccountDetails.getCurrentUser().getUsername());
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot users : dataSnapshot.getChildren())
                {
                    if(users.getKey().equals(String.valueOf(movie.getId()))) {
                        movieCount = users.getValue(String.class);
                        watchedMovieStatus = true;
                        if(!movieCount.equals("0")) {
                            watched.setText("x" + movieCount);
                            setTextViewDrawableColor(watched, R.color.colorAccent);
                          }
                        else {
                            watched.setText("Watched");
                            setTextViewDrawableColor(watched, R.color.black);
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

    public void checkWatchListStatus() {
        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<AccountStates> call = apiService.getAccountStates( movie.getId(),BuildConfig.THE_MOVIE_DB_API_TOKEN, session_id);
        call.enqueue(new Callback<AccountStates>() {
            @Override
            public void onResponse(Call<AccountStates> call, Response<AccountStates> response) {
                watchlistStatus = response.body().getWatchlist();
                if(watchlistStatus)
                    setTextViewDrawableColor(wish, R.color.colorAccent);

                Log.d("Errorhahaha", "response.raw().request().url()"+response.raw().request().url());
            }
            @Override
            public void onFailure(Call<AccountStates> call, Throwable t) {
                Log.d("Errorhahaha", call.request().url().toString());
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
        collapsingToolbarLayout.setTitle(movie.getTitle());
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
                    poster.setVisibility(View.VISIBLE);
                }
                if (scrollRange + verticalOffset == 0){
                    collapsingToolbarLayout.setTitle(movie.getTitle());
                    isShow = true;
                    poster.setVisibility(View.GONE);
                }else if (isShow){
                    collapsingToolbarLayout.setTitle(movie.getTitle());
                    isShow = false;
                    poster.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.watched: {
                afterWatchedPressed();
                break;
            }
            case R.id.wish: {
                afterWishPressed();
                break;
            }
            case R.id.list: {
                afterListPressed();
                break;
            }
            case R.id.thumbnail_image_header:{
                Intent intent = new Intent(DetailActivity.this, DetailActivityForCoverPhoto.class);
                intent.putExtra("Movie Id",movie.getId());
                startActivity(intent);
                break;
            }
            case R.id.director:{
                String url = "https://en.wikipedia.org/wiki/";
                directorName.replace(" ","_");
                Intent intent = new Intent(this,SignUpWebview.class); // we are using SignUpWebview Class becuase BrowserWebview is complex
                intent.putExtra("url",url+directorName);
                startActivity(intent);
                break;
            }
        }
    }

    private void afterListPressed() {
    }

    private void afterWatchedPressed() {
        final int[] count = {Integer.parseInt(movieCount)};
        final int[] time = new int[1];
        final String[] temp = new String[1];
        final DatabaseReference databaseUsers= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(AccountDetails.getCurrentUser().getUsername());
        final DatabaseReference databaseUsers2= FirebaseDatabase.getInstance().getReference().child("Total Time")
                .child(AccountDetails.getCurrentUser().getUsername());


        if(!movieCount.equals("0")){
            DialogMenu dialogMenu = new DialogMenu(this);
            dialogMenu.setListener(new DialogMenu.OnDialogMenuListener() {
                @Override
                public void onReWatchedPress() {
                    count[0]++;
                    temp[0] = String.valueOf(count[0]);
                    time[0] = Integer.parseInt(totalCount) + movieDetails.getRuntime();
                    databaseUsers.child(String.valueOf(movie.getId())).setValue(temp[0]);
                    databaseUsers2.setValue(String.valueOf(time[0]));
                    if(!StartUpActivity.seenMovies.contains(movie.getId().toString()))
                            StartUpActivity.seenMovies.add(movie.getId().toString());
                }

                @Override
                public void onNotWatchedPress() {
                    count[0]--;
                    temp[0] = String.valueOf(count[0]);
                    time[0] = Integer.parseInt(totalCount) - movieDetails.getRuntime();
                    databaseUsers.child(String.valueOf(movie.getId())).setValue(temp[0]);
                    databaseUsers2.setValue(String.valueOf(time[0]));
                    if(!String.valueOf(time[0]).equals("0")) {//not working
                        StartUpActivity.seenMovies.remove(movie.getId().toString());
                    }
                }
            });
        }
        else{
            count[0]++;
            temp[0] = String.valueOf(count[0]);
            setTextViewDrawableColor(watched, R.color.colorAccent);
            time[0] = Integer.parseInt(totalCount) + movieDetails.getRuntime();
            databaseUsers.child(String.valueOf(movie.getId())).setValue(temp[0]);
            databaseUsers2.setValue(String.valueOf(time[0]));
            if(!StartUpActivity.seenMovies.contains(movie.getId().toString()))
                StartUpActivity.seenMovies.add(movie.getId().toString());
        }
    }

    private void afterWishPressed() {
        final WatchListBody watchListBody = new WatchListBody();
        watchListBody.setMediaId(movie.getId());
        watchListBody.setMediaType("movie");
        if(watchlistStatus) {
            watchListBody.setWatchlist(false);
            setTextViewDrawableColor(wish, R.color.black);
        }
        else {
            watchListBody.setWatchlist(true);
            setTextViewDrawableColor(wish, R.color.colorAccent);
        }

        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<WatchListResponse> call = apiService.postWatchList(BuildConfig.THE_MOVIE_DB_API_TOKEN,session_id,watchListBody);
        call.enqueue(new Callback<WatchListResponse>() {
            @Override
            public void onResponse(Call<WatchListResponse> call, Response<WatchListResponse> response) {
                Integer status = response.body().getStatusCode();
                if(status == 1)
                {
                    watchlistStatus = true;
                }
                else if(status == 12)
                {
                    watchlistStatus=true;
                }
                else if(status == 13){
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
