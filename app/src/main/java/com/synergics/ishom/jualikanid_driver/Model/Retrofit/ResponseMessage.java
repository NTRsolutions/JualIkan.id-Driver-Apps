package com.synergics.ishom.jualikanid_driver.Model.Retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseMessage {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("messge") public String message;
    @SerializedName("data") public Data data;

    public class Data {
        @SerializedName("profile") public Profile profile;
        @SerializedName("history") public List<Saldo> history;
    }

    public class Profile {
        @SerializedName("id") public String id;
        @SerializedName("name") public String name;
        @SerializedName("saldo") public String saldo;
    }

    public class Saldo {
        @SerializedName("id") public String id;
        @SerializedName("name") public String name;
        @SerializedName("value") public String saldo;
    }
}
