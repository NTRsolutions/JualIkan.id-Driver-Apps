package com.synergics.ishom.jualikanid_driver.Model.Retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseBantuan {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("data") public ArrayList<Bantuan> data;

    public class Bantuan {
        @SerializedName("articel_id") public String articel_id;
        @SerializedName("articel_name") public String articel_name;
        @SerializedName("articel_user_level_id") public String articel_user_level_id;
        @SerializedName("articel_url") public String articel_url;
    }
}
