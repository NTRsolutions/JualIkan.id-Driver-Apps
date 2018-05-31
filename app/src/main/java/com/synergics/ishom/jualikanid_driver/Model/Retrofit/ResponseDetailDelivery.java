package com.synergics.ishom.jualikanid_driver.Model.Retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseDetailDelivery {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("message") public String message;
    @SerializedName("data") public Data data;

    public static class Data {
        @SerializedName("id") public int id;
        @SerializedName("code") public String code;
        @SerializedName("orders") public List<Order> orders;
        @SerializedName("koperasi") public Koperasi koperasi;
        @SerializedName("time") public Time time;
        @SerializedName("distance") public Distance distance;
        @SerializedName("biaya") public Biaya biaya;
        @SerializedName("status") public int status;
    }

    public static class Koperasi {
        @SerializedName("id") public String id;
        @SerializedName("name") public String name;
        @SerializedName("address") public String address;
        @SerializedName("lat") public String lat;
        @SerializedName("lng") public String lng;
    }

    public static class Time {
        @SerializedName("value") public int value;
        @SerializedName("text") public String text;
    }

    public static class Distance {
        @SerializedName("value") public int value;
        @SerializedName("text") public String text;
    }

    public static class Biaya {
        @SerializedName("value") public int value;
        @SerializedName("text") public String text;
    }

    public static class Order {
        @SerializedName("id") public String id;
        @SerializedName("user") public User user;
        @SerializedName("cart") public Cart cart;
        @SerializedName("lokasi") public Lokasi lokasi;

        public static class User {
            @SerializedName("id") public String id;
            @SerializedName("name") public String name;
            @SerializedName("image") public String image;
            @SerializedName("email") public String email;
            @SerializedName("phone") public String phone;
        }

        public static class Lokasi {
            @SerializedName("lokasi_name") public String lokasi_name;
            @SerializedName("lokasi_lat") public String lokasi_lat;
            @SerializedName("lokasi_lng") public String lokasi_lng;
        }

        public static class Cart {
            @SerializedName("items") public List<CartDetail> items;
            @SerializedName("total") public int total;
        }

        public static class CartDetail {
            @SerializedName("id") public String id;
            @SerializedName("fish_id") public String fish_id;
            @SerializedName("image") public String image;
            @SerializedName("name") public String name;
            @SerializedName("price") public int price;
            @SerializedName("qty") public int qty;
            @SerializedName("total_price") public int total_price;
        }
    }

}
