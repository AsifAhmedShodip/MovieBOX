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
import com.example.asif.movies.MoviePaginationAdapter;
import com.example.asif.movies.PaginationScrollListener;
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
 * Created by asif on 12-Apr-18.
 */

public class Upcoming extends Fragment{
    private static final String TAG = "MainActivity";
    private SwipeRefreshLayout swipeContainer;

    MoviePaginationAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;

    RecyclerView rv;
    ProgressBar progressBar;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 5;
    private int currentPage = PAGE_START;

    public Upcoming(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        rv =  view.findViewById(R.id.recycler);
        progressBar = view.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);

        adapter = new MoviePaginationAdapter(getActivity());

        //linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //rv.setLayoutManager(linearLayoutManager);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gridLayoutManager = new GridLayoutManager(getActivity(),4);
        } else {
            gridLayoutManager = new GridLayoutManager(getActivity(),8);
        }

        rv.setLayoutManager(gridLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(adapter);

        rv.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                currentPage = 1;
                adapter.clear();
                loadFirstPage();
                Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        loadFirstPage();

        return view;
    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<MoviesResponse> call = apiService.getUpcomingMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN,
                "en", currentPage,"US");

        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                // Got data. Send it to adapter

                List<Movie> results = fetchResults(response);
                TOTAL_PAGES = response.body().getTotalPages();
                progressBar.setVisibility(View.GONE);
                adapter.addAll(results);

                if (currentPage <= TOTAL_PAGES)
                    adapter.addLoadingFooter();
                else isLastPage = true;

                if (swipeContainer.isRefreshing()) {
                    swipeContainer.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                if (swipeContainer.isRefreshing()) {
                    swipeContainer.setRefreshing(false);
                }
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });

    }

    private List<Movie> fetchResults(Response<MoviesResponse> response) {
        MoviesResponse topRatedMovies = response.body();
        return topRatedMovies.getResults();
    }


    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<MoviesResponse> call = apiService.getUpcomingMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN,
                "en", currentPage,"US");

        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                List<Movie> results = fetchResults(response);
                adapter.addAll(results);

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                t.printStackTrace();
                // TODO: 08/11/16 handle failure
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}