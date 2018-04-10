package com.example.asif.movies.Profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.asif.movies.BuildConfig;
import com.example.asif.movies.R;
import com.example.asif.movies.adapter.MoviesAdapterForCoverPhoto;
import com.example.asif.movies.api.Client;
import com.example.asif.movies.api.Service;
import com.example.asif.movies.model.image.Backdrop;
import com.example.asif.movies.model.image.ImageResoponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivityForCoverPhoto extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MoviesAdapterForCoverPhoto adapter;
    private Integer movieId;
    private List<Backdrop> backdropList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_for_cover_photo);
        recyclerView =  findViewById(R.id.recycler);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            movieId = (int) b.get("Movie Id");
        }
        loadJSON();
    }

    private void initViews(List<Backdrop> backdrops) {
        adapter = new MoviesAdapterForCoverPhoto(DetailActivityForCoverPhoto.this, backdrops);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void loadJSON(){
            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<ImageResoponse> call = apiService.getImages(movieId, BuildConfig.THE_MOVIE_DB_API_TOKEN,"en","en,null");
            call.enqueue(new Callback<ImageResoponse>() {
                @Override
                public void onResponse(Call<ImageResoponse> call, Response<ImageResoponse> response) {
                    backdropList = response.body().getBackdrops();
                    initViews(backdropList);
                }

                @Override
                public void onFailure(Call<ImageResoponse> call, Throwable t) {
                    Log.d("Error" , t.getMessage());
                    Toast.makeText(getApplicationContext(), "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
    }
}
