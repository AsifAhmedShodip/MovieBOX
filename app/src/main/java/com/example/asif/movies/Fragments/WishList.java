package com.example.asif.movies.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.asif.movies.BuildConfig;
import com.example.asif.movies.LogIn;
import com.example.asif.movies.MainActivity;
import com.example.asif.movies.R;
import com.example.asif.movies.adapter.MoviesAdapter;
import com.example.asif.movies.api.Client;
import com.example.asif.movies.api.Service;
import com.example.asif.movies.model.Movie;
import com.example.asif.movies.model.MoviesResponse;
import com.example.asif.movies.model.WatchList;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by asif on 06-Apr-18.
 */

public class WishList extends Fragment {

    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private List<Movie> movieList;
    ProgressDialog pd,pd2;
    private SwipeRefreshLayout swipeContainer;
    public static Movie movieStatic;
    SharedPreferences sharedpreferences;
    public WishList() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        //final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frame);
        //frameLayout.setBackgroundColor(color);
        recyclerView =  view.findViewById(R.id.recycler);
        sharedpreferences = getActivity().getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        loadJSON();

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                loadJSON();
                Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void initViews(List<Movie> movies) {
        adapter = new MoviesAdapter(getContext(), movies);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 8));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (swipeContainer.isRefreshing()){
            swipeContainer.setRefreshing(false);
        }
        //pd.dismiss();
    }
    private void loadJSON(){
       /* pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();*/
        try{
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getActivity(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                return;
            }
                Client Client = new Client();
                Service apiService = Client.getClient().create(Service.class);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                String session_id = sharedpreferences.getString("Session_id","");
                Call<MoviesResponse> call = apiService.getWatchList(BuildConfig.THE_MOVIE_DB_API_TOKEN, session_id);
                call.enqueue(new Callback<MoviesResponse>() {
                    @Override
                    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                        if(!response.body().getResults().equals(null)) {
                            List<Movie> movies = response.body().getResults();
                            initViews(movies);
                        }
                    }

                    @Override
                    public void onFailure(Call<MoviesResponse> call, Throwable t) {
                        if (swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        }
                        //pd.dismiss();
                        Log.d("Error", t.getMessage());
                        Toast.makeText(getActivity(), "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                    }
                });
        }catch (Exception e){
            if (swipeContainer.isRefreshing()){
                swipeContainer.setRefreshing(false);
            }
            //pd.dismiss();
            Log.d("Error", e.getMessage());
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
