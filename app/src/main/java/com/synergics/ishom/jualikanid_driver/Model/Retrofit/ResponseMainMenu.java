package com.synergics.ishom.jualikanid_driver.Model.Retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseMainMenu {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("message") public String message;
    @SerializedName("data") public Data data;

    public static class Data {
        @SerializedName("driver") public Driver driver;
        @SerializedName("last_delivery") public Last_Delivery last_delivery;
    }

    public static class Driver {
        @SerializedName("id") public String id;
        @SerializedName("name") public String name;
        @SerializedName("image") public String image;
        @SerializedName("email") public String email;
        @SerializedName("phone") public String phone;
        @SerializedName("saldo") public int saldo;
        @SerializedName("order_total") public int order_total;
        @SerializedName("status") public int status;
        @SerializedName("text_status") public String text_status;
    }

    public static class Last_Delivery {
        @SerializedName("id") public String id;
        @SerializedName("code") public String code;
        @SerializedName("order_count") public int order_count;
        @SerializedName("date_time") public String date_time;
        @SerializedName("distance") public String distance;
        @SerializedName("time") public String time;
        @SerializedName("status") public int status;
    }

}
