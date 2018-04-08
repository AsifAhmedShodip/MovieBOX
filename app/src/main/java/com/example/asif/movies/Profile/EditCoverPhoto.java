package com.example.asif.movies.Profile;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.asif.movies.model.Account.AccountDetails;
import com.example.asif.movies.BuildConfig;
import com.example.asif.movies.R;
import com.example.asif.movies.adapter.MoviesAdapterForCoverPhotoSearch;
import com.example.asif.movies.api.Client;
import com.example.asif.movies.api.Service;
import com.example.asif.movies.model.Movie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCoverPhoto extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MoviesAdapterForCoverPhotoSearch adapter;
    private RecyclerView.Adapter rAdapter;
    private List<Movie> movieList = new ArrayList<>();
    ProgressDialog pd,pd2;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cover_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("From Which Movie?");
        recyclerView =  findViewById(R.id.recycler);
        loadWatchedMoviesIdFromFirebase();
        //loadJSON();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
               // loadJSON();
                Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initViews(List<Movie> movies) {
        adapter = new MoviesAdapterForCoverPhotoSearch(EditCoverPhoto.this, movies);

        if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 8));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (swipeContainer.isRefreshing()){
            swipeContainer.setRefreshing(false);
        }
        //pd.dismiss();
    }

    private void loadWatchedMoviesIdFromFirebase(){
        final HashMap<String ,String> hm = new HashMap();
        final DatabaseReference databaseUsers= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(AccountDetails.getCurrentUser().getUsername());
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot users : dataSnapshot.getChildren()){
                    hm.put(users.getKey(),new String(users.getValue().toString()));
                }
                sortHashMapByValues(hm);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void sortHashMapByValues(HashMap<String, String> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<String> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues,Collections.<String>reverseOrder());
        Collections.sort(mapKeys,Collections.<String>reverseOrder());

        HashMap<String, String> sortedMap = new HashMap<>();

        Iterator<String> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            String val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                String comp1 = passedMap.get(key);
                String comp2 = val;
                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    Log.d("Hash  ",key+"  "+val);
                    loadJSON(key);
                    break;
                }
            }
        }

    }

    //Is not sorting perfectly
    private void loadJSON(String movieID){
        /*pd = new ProgressDialog(getApplicationContext());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();*/
        try{
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                return;
            }
            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
                Call<Movie> call = apiService.getMovieDetails(Integer.parseInt(movieID),BuildConfig.THE_MOVIE_DB_API_TOKEN);
                call.enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> response) {
                        movieList.add(response.body());
                        initViews(movieList);
                    }

                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {
                        if (swipeContainer.isRefreshing()) {
                            swipeContainer.setRefreshing(false);
                        }
                        //pd.dismiss();
                        Log.d("Error", t.getMessage());
                        Toast.makeText(getApplicationContext(), "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                    }
                });
        }catch (Exception e){
            if (swipeContainer.isRefreshing()){
                swipeContainer.setRefreshing(false);
            }
            //pd.dismiss();
            Log.d("Error", e.getMessage());
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        //menuItem.expandActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setQueryHint("Search Movies");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
