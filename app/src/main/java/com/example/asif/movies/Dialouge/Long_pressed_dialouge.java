package com.example.asif.movies.Dialouge;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.asif.movies.R;

/**
 * Created by asif on 12-Apr-18.
 */

public class Long_pressed_dialouge implements View.OnClickListener {
    private Dialog dialog;
    private Long_pressed_dialouge.OnDialogMenuListener listener;
    public TextView watched,wished,list,rating,name,year,imdb,tmdb,rotten,meta;

    public Long_pressed_dialouge(Context context) {
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.long_pressed_popup);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        watched = (TextView) dialog.findViewById(R.id.watched);
        wished = (TextView) dialog.findViewById(R.id.wish);
        list = (TextView) dialog.findViewById(R.id.list);
        name = dialog.findViewById(R.id.name);
        name = dialog.findViewById(R.id.name);
        year = dialog.findViewById(R.id.year);
        imdb = dialog.findViewById(R.id.imdbrating);
        rotten = dialog.findViewById(R.id.rottenrating);
        tmdb = dialog.findViewById(R.id.tmdbrating);
        meta = dialog.findViewById(R.id.metarating);
        //rating = (TextView) dialog.findViewById(R.id.wish);
        watched.setOnClickListener(this);
        wished.setOnClickListener(this);
        list.setOnClickListener(this);
    }

    public void setListener(Long_pressed_dialouge.OnDialogMenuListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.watched:
                listener.onWatchedPress();
                //dialog.dismiss();
                break;
            case R.id.wish:
                listener.onWishedPress();
                //dialog.dismiss();
                break;
            case R.id.list:
                listener.onWishedPress();
                //dialog.dismiss();
                break;
        }
    }

    public interface OnDialogMenuListener{
        void onWatchedPress();
        void onWishedPress();
    }

}
