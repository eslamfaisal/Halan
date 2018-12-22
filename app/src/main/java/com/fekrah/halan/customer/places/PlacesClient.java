package com.fekrah.halan.customer.places;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesClient {


 // "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&rankby=distance&type=food&key=YOUR_API_KEY"
    String uri = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=مطعم&location=30.2529128,31.1646382&rankby=distance&key=AIzaSyAnKvay92-zyf4Or37UL6tsEF7BL8PiC6U";
  //  @GET("api/place/textsearch/json")

    @GET("api/place/textsearch/json")
    Call<Places> getFirstPlaces(
            @Query("query") String query,
            // @Query("radius") int radius,
            @Query("location") String latlng,
            @Query("key") String key,
            @Query("language") String language,
            @Query("rankby") String rankBy

    );


  @GET("api/place/nearbysearch/json?&rankby=distance&key=AIzaSyAnKvay92-zyf4Or37UL6tsEF7BL8PiC6U")
  Call<Places> getNearPlaces(
          @Query("language") String language,
          @Query("location") String latlng

  );

    @GET("api/place/textsearch/json")
    Call<Places> getNextPlaces(
            @Query("pagetoken") String token,
            @Query("key") String key

    );

}

//@GET("api/place/textsearch/json?query=مطعم&location=30.2529128,31.1646382&rankby=distance&key=AIzaSyAnKvay92-zyf4Or37UL6tsEF7BL8PiC6U")
