package com.example.asif.movies.starting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.asif.movies.Bottom_Navigation;
import com.example.asif.movies.R;
import com.example.asif.movies.model.Account.AccountDetails;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class StartUpActivity extends AppCompatActivity {
    TextView appName;
    SharedPreferences sharedpreferences;
    String session_id;
    RelativeLayout screen;
    public static List<String > seenMovies = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        StatusBarUtil.setTransparent(this);
        appName = findViewById(R.id.MBOX);
        screen = findViewById(R.id.screen);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/BerkshireSwash-Regular.ttf");
        appName.setTypeface(type);

        Animation animRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        screen.startAnimation(animRightIn);

        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.setUpdateFrom(UpdateFrom.GITHUB)
                    .setGitHubUserAndRepo("AsifAhmedShodip","MovieBOX")
                .setDisplay(Display.DIALOG)
                .start();

        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String s_id = sharedpreferences.getString("Session_id","");
        if(!s_id.equals("")){
            session_id = s_id;
            //accountDetails(session_id);
            Gson gson = new Gson();
            String json = sharedpreferences.getString("Account Details", "");
            AccountDetails obj = gson.fromJson(json, AccountDetails.class);
            AccountDetails.setCurrentUser(obj);
            firebaseDataLoadUp(obj.getUsername());
            Intent i = new Intent (getApplicationContext(),Bottom_Navigation.class);
            startActivity(i);
        }
        else{
            Intent i = new Intent (getApplicationContext(),LogIn.class);
            startActivity(i);
        }
    }

    private void firebaseDataLoadUp(String username) {
        final DatabaseReference databaseUsers= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(username);
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot users : dataSnapshot.getChildren()){
                    if(!users.getValue().toString().equals("0")) {
                        seenMovies.add(users.getKey().toString());
                        Log.d("Array" ,users.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Animation animRotateIn_big = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        screen.startAnimation(animRotateIn_big);
        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String s_id = sharedpreferences.getString("Session_id","");
        if(!s_id.equals("")){
            session_id = s_id;
            Gson gson = new Gson();
            String json = sharedpreferences.getString("Account Details", "");
            AccountDetails obj = gson.fromJson(json, AccountDetails.class);
            AccountDetails.setCurrentUser(obj);
            Intent i = new Intent (getApplicationContext(),Bottom_Navigation.class);
            startActivity(i);
        }
        else{
            Intent i = new Intent (getApplicationContext(),LogIn.class);
            startActivity(i);
        }
    }
}
