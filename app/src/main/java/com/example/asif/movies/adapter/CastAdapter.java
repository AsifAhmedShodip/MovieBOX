package com.example.asif.movies.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.asif.movies.DetailActivity;
import com.example.asif.movies.R;
import com.example.asif.movies.WebView_Link.BrowserWebview;
import com.example.asif.movies.WebView_Link.SignUpWebview;
import com.example.asif.movies.model.Cast_crew.Cast;
import com.example.asif.movies.model.Movie;

import java.util.List;

/**
 * Created by asif on 08-Apr-18.
 */

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.MyViewHolder>{

    private Context mContext;
    private List<Cast> castList;

    public CastAdapter(Context mContext,List<Cast> castList){
        this.mContext = mContext;
        this.castList = castList;
    }
    @Override
    public CastAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.cast_card, parent, false);
        return new CastAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CastAdapter.MyViewHolder viewHolder, final int i) {

        String poster = "https://image.tmdb.org/t/p/w185" + castList.get(i).getProfilePath();
        Log.d("Error",poster);
        Glide.with(mContext)
                .load(poster)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.load)
                        .apply(RequestOptions.circleCropTransform())
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(viewHolder.thumbnail);
        viewHolder.castName.setText(castList.get(i).getName());
        viewHolder.charecterName.setText(castList.get(i).getCharacter());
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView castName, charecterName;
        public ImageView thumbnail;
        public CardView cardView;
        RelativeLayout relativeLayout;

        public MyViewHolder(View view){
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            castName = view.findViewById(R.id.castName);
            charecterName = view.findViewById(R.id.charecterName);
            relativeLayout = view.findViewById(R.id.relative);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://en.wikipedia.org/wiki/";
                    String name = castList.get(getAdapterPosition()).getName();
                    name.replace(" ","_");
                    Intent intent = new Intent(mContext,SignUpWebview.class); // we are using SignUpWebview Class becuase BrowserWebview is complex
                    intent.putExtra("url",url+name);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
