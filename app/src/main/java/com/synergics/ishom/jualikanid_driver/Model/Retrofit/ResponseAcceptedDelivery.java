package com.synergics.ishom.jualikanid_driver.Model.Retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asmarasusanto on 1/18/18.
 */

public class ResponseAcceptedDelivery {
    @SerializedName("response") public int response;
    @SerializedName("status") public boolean status;
    @SerializedName("message") public String message;
}
