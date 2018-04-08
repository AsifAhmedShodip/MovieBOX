package com.example.asif.movies.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.asif.movies.model.Account.AccountDetails;
import com.example.asif.movies.R;
import com.example.asif.movies.model.image.Backdrop;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by asif on 07-Apr-18.
 */

public class MoviesAdapterForCoverPhoto extends RecyclerView.Adapter<MoviesAdapterForCoverPhoto.MyViewHolder>{
    private Context mContext;
    private List<Backdrop> backdropList;

    public MoviesAdapterForCoverPhoto(Context mContext, List<Backdrop> backdropList){
        this.mContext = mContext;
        this.backdropList = backdropList;
    }
    @Override
    public MoviesAdapterForCoverPhoto.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.backdrop_card, parent, false);
        return new MoviesAdapterForCoverPhoto.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviesAdapterForCoverPhoto.MyViewHolder viewHolder, final int i) {

        String poster = "https://image.tmdb.org/t/p/w500" + backdropList.get(i).getFilePath();

        Glide.with(mContext)
                .load(poster)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.load)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(viewHolder.picture);

        viewHolder.picture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                    final Backdrop clickedDataItem = backdropList.get(i);
                    final DatabaseReference databaseUsers= FirebaseDatabase.getInstance().getReference().child("Users")
                            .child("Cover Photo")
                            .child(AccountDetails.getCurrentUser().getUsername());

                    databaseUsers.setValue(clickedDataItem.getFilePath());
                   // ((Activity)mContext).finish();
                    AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(((Activity)mContext));
                    alertDialogBuilder.setMessage("Set This image as your Cover Picture?");
                    alertDialogBuilder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    final DatabaseReference databaseUsers= FirebaseDatabase.getInstance().getReference().child("Users")
                                            .child("Cover Photo")
                                            .child(AccountDetails.getCurrentUser().getUsername());

                                    databaseUsers.setValue(clickedDataItem.getFilePath());
                                    arg0.dismiss();

                                }
                            });

                    alertDialogBuilder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
        });


    }

    @Override
    public int getItemCount() {
        return backdropList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, userrating;
        public ImageView picture;

        public MyViewHolder(View view){
            super(view);
            picture = (ImageView) view.findViewById(R.id.picture);
        }
    }
}

