package com.example.asif.movies.Fragments;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import com.example.asif.movies.BuildConfig;
import com.example.asif.movies.MainActivity;
import com.example.asif.movies.R;
import com.example.asif.movies.adapter.MoviesAdapter;
import com.example.asif.movies.api.Client;
import com.example.asif.movies.api.Service;
import com.example.asif.movies.model.Movie;
import com.example.asif.movies.model.MoviesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by asif on 06-Apr-18.
 */

public class AllMovieList extends Fragment{
    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private RecyclerView.Adapter rAdapter;
    private List<Movie> movieList;
    ProgressDialog pd;
    private SwipeRefreshLayout swipeContainer;
    int page = 0;
    List<Movie> movies = new ArrayList<>();
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    public AllMovieList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        //final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frame);
        //frameLayout.setBackgroundColor(color);
        recyclerView =  view.findViewById(R.id.recycler);
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

        adapter = new MoviesAdapter(getContext(), movies);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 8));
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                /*Log.d("Error  ", "dy "+String.valueOf(dy));
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                    totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    pastVisiblesItems =((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            //loading = false;
                            Log.d("Error", "Last Item Wow !"+visibleItemCount+" "+totalItemCount
                                    +" "+pastVisiblesItems);
                            loadJSON();
                        }
                    }
                }*/


                currentItems = recyclerView.getLayoutManager().getChildCount();
                totalItems = recyclerView.getLayoutManager().getItemCount();
                scrollOutItems = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems == totalItems))
                {
                    isScrolling = false;
                    loadJSON();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling = true;
                }
            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (swipeContainer.isRefreshing()){
            swipeContainer.setRefreshing(false);
        }

        return view;
    }

    private void loadJSON(){
        /*
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();*/
        try{
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getActivity(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                return;
            }
            page++;
                Client Client = new Client();
                Service apiService = Client.getClient().create(Service.class);
                Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN,
                        "en", page);
                call.enqueue(new Callback<MoviesResponse>() {
                    @Override
                    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                        //movies = response.body().getResults();
                        for (int j = 0; j < response.body().getResults().size(); j++) {
                            movies.add(response.body().getResults().get(j));
                        }
                        if (swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        }
                        adapter = new MoviesAdapter(getActivity(), movies);
                        recyclerView.setAdapter(adapter);
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
