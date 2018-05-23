package com.synergics.ishom.jualikanid_driver.Controller.GMapsTrack;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by asmarasusanto on 10/8/17.
 */

public class GMapsAdress {

    @SerializedName("results") public List<Route> results;

    public class Route {
        @SerializedName("formatted_address") public String formatted_address;
    }

}
