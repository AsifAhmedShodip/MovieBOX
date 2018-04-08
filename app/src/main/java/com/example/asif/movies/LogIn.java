package com.example.asif.movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asif.movies.model.Account.AccountDetails;
import com.example.asif.movies.api.Client;
import com.example.asif.movies.api.Service;
import com.example.asif.movies.authentication.Request_Token;
import com.example.asif.movies.authentication.Session_Id;
import com.example.asif.movies.model.ListResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogIn extends AppCompatActivity {
    private Button button;
    TextView register;
    String rToken;
    String session_id = "";
    private ListResponse listResponse;
    public String string;

    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        button = findViewById(R.id.signinbutton);
        register = findViewById(R.id.signUpbutton);

        //config();
        //accountDetails();
        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s_id = sharedpreferences.getString("Session_id","");
                if(!s_id.equals("")){
                    session_id = s_id;
                    accountDetails(session_id);
                }
                else{
                    checkToken();
                    //config();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //config();
                /*String url = "https://www.themoviedb.org/account/signup";
                Intent intent = new Intent(getApplicationContext(),SignUpWebview.class);
                intent.putExtra("url",url);
                startActivity(intent);*/
            }
        });
    }

    private void checkToken() {
        String token = sharedpreferences.getString("Token","");
        if(!token.equals("")){
            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<Session_Id> call = apiService.getSessionID(BuildConfig.THE_MOVIE_DB_API_TOKEN,token);
            call.enqueue(new Callback<Session_Id>() {
                @Override
                public void onResponse(Call<Session_Id> call, Response<Session_Id> response) {
                    if(response.body() instanceof Session_Id) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("Session_id", response.body().getSessionId());
                        editor.commit();
                        Log.d("Error", response.body().getSessionId());
                        session_id = response.body().getSessionId();
                        accountDetails(session_id);
                    }
                    else {
                        config();
                    }
                }

                @Override
                public void onFailure(Call<Session_Id> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(getApplicationContext(),"Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }
        else {
            config();
        }
    }

  /*  private void checkWatchList() {
        boolean watchedMovieList = false;
        for(int i=0;i<listResponse.getList().size();i++)
        {
            if(listResponse.getList().get(i).getName().equals("Watched Movies"))
            {
                watchedMovieList = true;
            }
        }
        if(!watchedMovieList)
        {
            CreateList createList = new CreateList();
            createList.setName("Watched Movies");
            createList.setDescription("Watched Movies");
            createList.setLanguage("en");
            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<CreateListResponse> call = apiService.creatList(BuildConfig.THE_MOVIE_DB_API_TOKEN,session_id,createList);
            call.enqueue(new Callback<CreateListResponse>() {
                @Override
                public void onResponse(Call<CreateListResponse> call, Response<CreateListResponse> response) {
                    if(response.body().getStatusCode() == 1)
                    {
                        Log.d("Error", "DONE CREATING");
                    }
                }

                @Override
                public void onFailure(Call<CreateListResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(LogIn.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }*/

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
                Toast.makeText(LogIn.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //loading up firebase to use in next activity
    private void firebaseDataLoadUp(String username) {
        final DatabaseReference databaseUsers= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(username);
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot users : dataSnapshot.getChildren()){
                    //do something
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void config() {

        try{
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                return;
            }

            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<Request_Token> call = apiService.getRequestToken(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<Request_Token>() {
                @Override
                public void onResponse(Call<Request_Token> call, Response<Request_Token> response) {
                    Request_Token token = (Request_Token) response.body();
                    rToken = token.getRequestToken();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("Token", rToken);
                    editor.commit();
                    String url = "https://www.themoviedb.org/authenticate/"+token.getRequestToken();
                    Intent i = new Intent (Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }

                @Override
                public void onFailure(Call<Request_Token> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(getApplicationContext(),"Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
