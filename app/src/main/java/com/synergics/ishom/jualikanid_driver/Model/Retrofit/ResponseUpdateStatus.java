package com.synergics.ishom.jualikanid_driver.Model.Retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseUpdateStatus {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("message") public String message;
    @SerializedName("text_status") public String text_status;
    @SerializedName("driver_status") public int driver_status;
}
