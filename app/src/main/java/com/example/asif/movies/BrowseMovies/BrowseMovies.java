package com.example.asif.movies.BrowseMovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.asif.movies.Profile.EditCoverPhoto;
import com.example.asif.movies.R;
import com.example.asif.movies.SearchActivity;
import com.example.asif.movies.model.Movie;
import com.example.asif.movies.starting.LogIn;

import java.util.ArrayList;
import java.util.List;

public class BrowseMovies extends Fragment {

    public static Movie movieStatic;
    SharedPreferences sharedpreferences;
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    private ViewPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView profile_pic , cover_photo;

    public BrowseMovies() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme_NoActionBar);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.activity_main_page, null, false);

        collapsingToolbarLayout = view.findViewById(R.id.htab_collapse_toolbar);

        toolbar = (Toolbar) view.findViewById(R.id.htab_toolbar);
        appBarLayout = view.findViewById(R.id.htab_appbar);
        tabLayout = view.findViewById(R.id.htab_tabs);

        //setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setCollapsible(false);

        //profile_pic = findViewById(R.id.profile_pic);
        cover_photo = view.findViewById(R.id.htab_header);
        mViewPager = (ViewPager) view.findViewById(R.id.htab_viewpager);
        setupViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);
        /*tabLayout.getTabAt(0).setIcon(R.drawable.ic_wish_color);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_movies);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_action_list2);*/

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return toolbarClick(item);
            }
        });

        //have to change
        /*String pic = "https://secure.gravatar.com/avatar/" + AccountDetails.getCurrentUser().getAvatar().getGravatar().getHash()
                                        +".jpg?s=64";*/

        /*Glide.with(getApplicationContext())
                .load(pic)
                .apply(RequestOptions.circleCropTransform())
                .into(profile_pic);*/


        setCoverPhoto();
        return view;
    }

    private void setCoverPhoto() {

        sharedpreferences = (getActivity()).getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String cover = sharedpreferences.getString("Cover Photo","");
        String poster0 = "https://image.tmdb.org/t/p/w300" + cover;
        Glide.with(BrowseMovies.this)
                .load(poster0)
                .thumbnail(0.1f)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.load)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(cover_photo);

        String poster = "https://image.tmdb.org/t/p/original" + cover;
        Log.d("Error", poster);

        Glide.with(BrowseMovies.this)
                .load(poster)
                .thumbnail(0.1f)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.load)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(cover_photo);
    }

    private boolean toolbarClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                Intent intent = new Intent(getActivity(),SearchActivity.class);
                startActivity(intent);
                break;

            case R.id.edit_cover_photo:{
                Intent i = new Intent(getActivity(),EditCoverPhoto.class);
                startActivity(i);
                break;
            }
            case R.id.logout:{
                sharedpreferences = (getActivity()).getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(getActivity(),LogIn.class);
                startActivity(i);
                break;
            }
        }
        return true;
    }


    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter((getActivity()).getSupportFragmentManager());
        adapter.addFrag(new TopRated(),"Top Rated");
        adapter.addFrag(new PopularMovies(),"Popular");
        adapter.addFrag(new NowPlayingMovies(),"Now Playing");
        adapter.addFrag(new Upcoming(),"Up Coming");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /*@Override
    public void onBackPressed() {
        (getActivity()).moveTaskToBack(true);
        getActivity().finish();
    }*/

    private static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String>  mFragmentTitleList  = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment,String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
