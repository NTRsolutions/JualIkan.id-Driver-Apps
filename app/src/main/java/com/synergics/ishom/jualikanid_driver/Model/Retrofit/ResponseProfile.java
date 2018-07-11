package com.synergics.ishom.jualikanid_driver.Model.Retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseProfile {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("profile") public Data profile;

    public static class Data {

        public Data() {}

        @SerializedName("driver_id") public String driver_id;
        @SerializedName("driver_full_name") public String driver_full_name;
        @SerializedName("driver_phone") public String driver_phone;
        @SerializedName("driver_email") public String driver_email;
        @SerializedName("driver_password") public String driver_password;
        @SerializedName("driver_device_id") public String driver_device_id;
        @SerializedName("driver_image") public String driver_image;
        @SerializedName("driver_koperasi_id") public String driver_koperasi_id;
        @SerializedName("driver_vehicle_weight") public String driver_vehicle_weight;
        @SerializedName("driver_address") public String driver_address;
        @SerializedName("driver_track_id") public String driver_track_id;
        @SerializedName("driver_saldo") public String driver_saldo;
    }


}
