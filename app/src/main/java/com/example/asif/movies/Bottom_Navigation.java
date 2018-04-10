package com.example.asif.movies;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.asif.movies.Fragments.AllMovieList;
import com.example.asif.movies.Fragments.WishList;
import com.jaeger.library.StatusBarUtil;

public class Bottom_Navigation extends AppCompatActivity {

    private ActionBar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom__navigation);
        StatusBarUtil.setTransparent(this);

        toolbar = getSupportActionBar();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        //toolbar.setTitle("Shop");
        loadFragment(new MainPage(navigation));
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
                case R.id.navigation_shop:
                    //toolbar.setTitle("Shop");
                    loadFragment(new MainPage());
                    return true;
                case R.id.navigation_gifts:
                    //toolbar.setTitle("My Gifts");
                    loadFragment(new WishList());
                    return true;
                case R.id.navigation_cart:
                    //toolbar.setTitle("Cart");
                    loadFragment(new AllMovieList());
                    return true;
                case R.id.navigation_profile:
                    //toolbar.setTitle("Profile");
                    return true;
            }
            return false;
        }
    };
}
