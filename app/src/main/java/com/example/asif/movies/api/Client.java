package com.example.asif.movies.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by asif on 30-Mar-18.
 */

public class Client {
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static final String BASE_URL_Omdb = "http://www.omdbapi.com/";
    public static Retrofit retrofit = null;
    public static Retrofit retrofit01 = null;

    public static Retrofit getClient(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientOmdb(){
        if (retrofit01 == null){
            retrofit01 = new Retrofit.Builder()
                    .baseUrl(BASE_URL_Omdb)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit01;
    }
}