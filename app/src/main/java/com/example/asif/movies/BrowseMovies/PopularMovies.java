package com.example.asif.movies.BrowseMovies;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.asif.movies.BuildConfig;
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

import static com.example.asif.movies.Bottom_Navigation.navigation;

/**
 * Created by asif on 06-Apr-18.
 */

public class PopularMovies extends Fragment{
    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    ProgressDialog pd;
    private SwipeRefreshLayout swipeContainer;
    int page = 0;
    List<Movie> movies = new ArrayList<>();
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    ProgressBar progressBar;
    int totalPages = 10;

    public PopularMovies() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        recyclerView =  view.findViewById(R.id.recycler);
        progressBar = view.findViewById(R.id.progress);
        loadJSON();

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                page = 0;
                movies.clear();
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
                if (dy > 0 && navigation.isShown()) {
                    navigation.setVisibility(View.GONE);
                } else if (dy < 0 ) {
                    navigation.setVisibility(View.VISIBLE);

                }

                currentItems = recyclerView.getLayoutManager().getChildCount();
                totalItems = recyclerView.getLayoutManager().getItemCount();
                scrollOutItems = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems == totalItems))
                {
                    isScrolling = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(page <= totalPages)
                                loadJSON();
                        }
                    }, 0);
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
        progressBar.setVisibility(View.VISIBLE);
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
                        "en", page,"US");
                call.enqueue(new Callback<MoviesResponse>() {
                    @Override
                    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                        totalPages = response.body().getTotalPages();
                        for (int j = 0; j < response.body().getResults().size(); j++) {
                            movies.add(response.body().getResults().get(j));
                        }
                        if (swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        }
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
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
            Log.d("Error", e.getMessage());
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
