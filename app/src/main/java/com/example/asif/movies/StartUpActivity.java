package com.example.asif.movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asif.movies.api.Client;
import com.example.asif.movies.api.Service;
import com.example.asif.movies.model.Account.AccountDetails;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartUpActivity extends AppCompatActivity {
    TextView appName;
    SharedPreferences sharedpreferences;
    String session_id;
    RelativeLayout screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        appName = findViewById(R.id.MBOX);
        screen = findViewById(R.id.screen);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/BerkshireSwash-Regular.ttf");
        appName.setTypeface(type);

        Animation animRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        screen.startAnimation(animRightIn);

        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String s_id = sharedpreferences.getString("Session_id","");
        if(!s_id.equals("")){
            session_id = s_id;
            accountDetails(session_id);
        }
        else{
            Intent i = new Intent (getApplicationContext(),LogIn.class);
            startActivity(i);
        }
    }

    public void accountDetails(String s_id) {
        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<AccountDetails> call = apiService.getAccountDetails(BuildConfig.THE_MOVIE_DB_API_TOKEN,s_id);
        call.enqueue(new Callback<AccountDetails>() {
            @Override
            public void onResponse(Call<AccountDetails> call, Response<AccountDetails> response) {
                AccountDetails.setCurrentUser(response.body());
                //if(response.body().getUsername()!=null)
                //firebaseDataLoadUp(response.body().getUsername());

                Intent i = new Intent (getApplicationContext(),MainPage.class);
                startActivity(i);
            }

            @Override
            public void onFailure(Call<AccountDetails> call, Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(StartUpActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
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
            accountDetails(session_id);
        }
        else{
            Intent i = new Intent (getApplicationContext(),LogIn.class);
            startActivity(i);
        }
    }
}
