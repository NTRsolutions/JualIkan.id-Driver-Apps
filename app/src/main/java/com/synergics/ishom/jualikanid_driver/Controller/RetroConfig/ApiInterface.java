package com.synergics.ishom.jualikanid_driver.Controller.RetroConfig;

import com.synergics.ishom.jualikanid_driver.Controller.GMapsTrack.GMapsAdress;
import com.synergics.ishom.jualikanid_driver.Controller.GMapsTrack.GMapsDirectionResponse;
import com.synergics.ishom.jualikanid_driver.Model.Object.MidtransPayment;
import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseLogin;
import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseMidtransSnap;
import com.synergics.ishom.jualikanid_driver.Model.TrackMaps.Direction;
import com.synergics.ishom.jualikanid_driver.Model.TrackMaps.NearbyLocation;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by asmarasusanto on 10/8/17.
 */

public interface ApiInterface {

    //================================= Google Maps endpoint web service ============================================//

    @GET("directions/json?key=AIzaSyBoFfYEuwzXaVwF07dt30KvYZM9vJXUGb0")
    Call<Direction> getDirection(@Query("origin") String origin,
                                 @Query("destination") String destionation,
                                 @Query("waypoints") String waypoint,
                                 @Query("sensor") boolean sensor,
                                 @Query("mode") String mode);

    @GET("directions/json?key=AIzaSyBoFfYEuwzXaVwF07dt30KvYZM9vJXUGb0")
    Call<GMapsDirectionResponse> getDirection(@Query("origin") String origin,
                                              @Query("destination") String destionation,
                                              @Query("sensor") boolean sensor,
                                              @Query("mode") String mode);

    @GET("geocode/json?key=AIzaSyBoFfYEuwzXaVwF07dt30KvYZM9vJXUGb0")
    Call<GMapsAdress> getAddress(@Query("latlng") String origin,
                                 @Query("sensor") boolean sensor);

    @GET("place/nearbysearch/json?key=AIzaSyBoFfYEuwzXaVwF07dt30KvYZM9vJXUGb0")
    Call<NearbyLocation> getNearby(@Query("location") String location,
                                   @Query("radius") String radius,
                                   @Query("types") String type);

    //================================= JualIkan.id endpoint web service ============================================//

    @POST("checkout.php")
    Call<ResponseMidtransSnap> snapMidtrans(@Body MidtransPayment body);

    //====================================  midtrans endpoint web service  ===========================================//

    @Multipart
    @POST("login.php")
    Call<ResponseLogin> login(@Part("phone") RequestBody email,
                              @Part("password") RequestBody pass,
                              @Part("device_id") RequestBody device_id);


}
