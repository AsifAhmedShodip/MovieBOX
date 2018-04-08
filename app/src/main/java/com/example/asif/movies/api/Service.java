package com.example.asif.movies.api;

import com.example.asif.movies.model.Account.AccountDetails;
import com.example.asif.movies.authentication.Request_Token;
import com.example.asif.movies.authentication.Session_Id;
import com.example.asif.movies.model.AccountStates;
import com.example.asif.movies.model.Cast_crew.CastResponse;
import com.example.asif.movies.model.CheckItemStatus;
import com.example.asif.movies.model.CreateList;
import com.example.asif.movies.model.CreateListResponse;
import com.example.asif.movies.model.ListResponse;
import com.example.asif.movies.model.MediaID;
import com.example.asif.movies.model.Movie;
import com.example.asif.movies.model.MoviesResponse;
import com.example.asif.movies.model.OmdbMovieResponse;
import com.example.asif.movies.model.WatchListBody;
import com.example.asif.movies.model.WatchListResponse;
import com.example.asif.movies.model.MovieVideoResponse;
import com.example.asif.movies.model.image.ImageResoponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by asif on 30-Mar-18.
 */

public interface Service {
    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("language") String language,
                                          @Query("page") int page);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("authentication/token/new")
    Call<Request_Token> getRequestToken(@Query("api_key") String apikey);

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails (@Path("movie_id") int movie_id,@Query("api_key") String apikey);

    @GET("account")
    Call<AccountDetails> getAccountDetails(@Query("api_key") String apikey,@Query("session_id") String session_id);

    @GET("account/{account_id}/watchlist/movies")
    Call<MoviesResponse> getWatchList(@Query("api_key") String apikey , @Query("session_id") String sessionid);

    @GET("authentication/session/new")
    Call<Session_Id> getSessionID(@Query("api_key") String apikey , @Query("request_token") String token);

    @POST("account/{account_id}/watchlist")
    Call<WatchListResponse> postWatchList(@Query("api_key") String apikey , @Query("session_id") String session_id,
                                          @Body WatchListBody watchListBody );

    @GET("movie/{movie_id}/account_states")
    Call<AccountStates> getAccountStates (@Path("movie_id") int movie_id,@Query("api_key") String apikey ,
                                          @Query("session_id") String session_id);

    @GET("account/{account_id}/lists")
    Call<ListResponse> getListResponse(@Query("api_key") String apikey,@Query("session_id") String session_id);

    @POST("list")
    Call<CreateListResponse> creatList(@Query("api_key") String apikey , @Query("session_id") String session_id,
                                       @Body CreateList createList);

    @GET("list/{list_id}/item_status")
    Call<CheckItemStatus> checkItemStatus(@Path("list_id") int list_id,@Query("api_key") String apikey ,
                                          @Query("movie_id") int movie_id);

    @POST("list/{list_id}/add_item")
    Call<WatchListResponse> addWatchedMovies(@Path("list_id") int list_id,@Query("api_key") String apikey,
                                             @Query("session_id") String session_id , @Body MediaID mediaID);

    @POST("list/{list_id}/remove_item")
    Call<WatchListResponse> deleteWatchedMovies(@Path("list_id") int list_id,@Query("api_key") String apikey,
                                             @Query("session_id") String session_id , @Body MediaID mediaID);

    @GET("search/movie")
    Call<MoviesResponse> getSearchResult(@Query("api_key") String apikey , @Query("query") String query);


    //OMDB
    @GET(".")
    Call<OmdbMovieResponse> getImdbRating(@Query("apikey") String apikey , @Query("i") String query);

    @GET("movie/{movie_id}/videos")
    Call<MovieVideoResponse> getMovieVideoResponse(@Path("movie_id") int movie_id, @Query("api_key") String apikey);

    @GET("movie/{movie_id}/images")
    Call<ImageResoponse> getImages(@Path("movie_id") int movie_id,@Query("api_key") String apikey,
                                   @Query("language") String language , @Query("include_image_language")
                                   String include_image_language) ;

    @GET("movie/{movie_id}/credits")
    Call<CastResponse> getCastDetails(@Path("movie_id") int movie_id ,@Query("api_key") String apikey);
}
