package com.example.asif.movies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.asif.movies.Account.AccountDetails;
import com.example.asif.movies.api.Client;
import com.example.asif.movies.api.Service;
import com.example.asif.movies.authentication.Request_Token;
import com.example.asif.movies.authentication.Session_Id;
import com.example.asif.movies.model.CreateList;
import com.example.asif.movies.model.CreateListResponse;
import com.example.asif.movies.model.ListMovie;
import com.example.asif.movies.model.ListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogIn extends AppCompatActivity {
    private Button button;
    String rToken;
    static String session_id = "b5bda44a7e59a9b33e9a70399724ae27c4046540";
    private AccountDetails accountDetails;
    private ListResponse listResponse;
    public String string;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        button = findViewById(R.id.button);

        //config();
        accountDetails();
        getList();
        //checkWatchList();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void checkWatchList() {
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
    }

    public void accountDetails() {
        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<AccountDetails> call = apiService.getAccountDetails(BuildConfig.THE_MOVIE_DB_API_TOKEN,session_id);
        call.enqueue(new Callback<AccountDetails>() {
            @Override
            public void onResponse(Call<AccountDetails> call, Response<AccountDetails> response) {
                 accountDetails = response.body();
                 string = response.body().getUsername();
            }

            @Override
            public void onFailure(Call<AccountDetails> call, Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(LogIn.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getList() {
        Client Client = new Client();
        Service apiService = Client.getClient().create(Service.class);
        Call<ListResponse> call = apiService.getListResponse(BuildConfig.THE_MOVIE_DB_API_TOKEN,session_id);
        call.enqueue(new Callback<ListResponse>() {
            @Override
            public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                listResponse = response.body();
                checkWatchList();
            }

            @Override
            public void onFailure(Call<ListResponse> call, Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(LogIn.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
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
