package com.teincstudios.tickit.rest;


import com.teincstudios.tickit.models.InternetTimeResponse;
import com.teincstudios.tickit.models.JointResponse;
import com.teincstudios.tickit.models.ReservationResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by yanchummar on 1/14/18.
 */

public interface ApiInterface {

    @GET("getNearbySpots.php")
    Call<JointResponse> getNearbySpots(@Query("lat") String lat, @Query("lng") String lng, @Query("type") String type);

    @GET("internetTime.php")
    Call<InternetTimeResponse> getInternetTime();

    @FormUrlEncoded
    @POST("addReservation.php")
    Call<String> addReservation(@Field("hash") String hash, @Field("spotId") String spotId, @Field("spotName") String spotName, @Field("spotLocation") String spotLocation,
                                @Field("name") String name, @Field("email") String email, @Field("phone") String phone,
                                @Field("foodieCount") int foodieCount, @Field("timeSlot") String timeSlot, @Field("bookingDate") long bookingDate,
                                @Field("freeBooking") boolean freeBooking, @Field("cost") int cost, @Field("tipAmount") int tipAmount);

    @GET("getReservationForHash.php")
    Call<ReservationResponse> getReservationForHash(@Query("hash") String hash);

    @GET("getReservationById.php")
    Call<ReservationResponse> getReservationById(@Query("resId") int resId);

    @POST("addUser.php")
    Call<String> addUser();
}