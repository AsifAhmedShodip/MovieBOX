package com.example.asif.movies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.asif.movies.R;
import com.example.asif.movies.model.image.Backdrop;

import java.util.List;

/**
 * Created by asif on 26-Apr-18.
 */

public class BackdropViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Backdrop> backdropList;

    public BackdropViewPagerAdapter(Context context, List<Backdrop> backdropList){
        this.context = context;
        this.backdropList = backdropList;
    }
    @Override
    public int getCount() {
        return backdropList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.backdrop_card,null);
        ImageView  imageView = view.findViewById(R.id.picture);
        TextView number = view.findViewById(R.id.number);

        number.setText(position+1+"/"+backdropList.size());

        String poster = "https://image.tmdb.org/t/p/original" + backdropList.get(position).getFilePath();

        Glide.with(context)
                .load(poster)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.load)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(imageView);

        ViewPager vp = (ViewPager) container;
        vp.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
