package com.example.asif.movies;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.asif.movies.BrowseMovies.BrowseMovies;
import com.example.asif.movies.BrowseMovies.PopularMovies;
import com.example.asif.movies.Fragments.WishList;

public class Bottom_Navigation extends AppCompatActivity {

    private ActionBar toolbar;
    public static BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom__navigation);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        toolbar = getSupportActionBar();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_browse);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        //toolbar.setTitle("Shop");
        loadFragment(new BrowseMovies());
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_browse:
                    //toolbar.setTitle("Shop");
                    loadFragment(new BrowseMovies());
                    return true;
                case R.id.navigation_list:
                    //toolbar.setTitle("My Gifts");
                    loadFragment(new WishList());
                    return true;
                case R.id.navigation_profile:
                    //toolbar.setTitle("Cart");
                    loadFragment(new PopularMovies());
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        finish();
    }
}
