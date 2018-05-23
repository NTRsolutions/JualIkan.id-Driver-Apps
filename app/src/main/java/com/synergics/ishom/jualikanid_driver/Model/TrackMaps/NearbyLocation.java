package com.synergics.ishom.jualikanid_driver.Model.TrackMaps;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asmarasusanto on 10/8/17.
 */

public class NearbyLocation {

    @SerializedName("results") public List<Near> routes;

    public class Near {
        @SerializedName("geometry") public Geometry geometry;
        @SerializedName("place_id") public String placeId;
        @SerializedName("name") public String nama;
        @SerializedName("vicinity") public String alamat;
        @SerializedName("photos") public ArrayList<Photo> photos;
        @SerializedName("icon") public String icon;
    }

    public class Geometry {
        @SerializedName("location") public Location location;
    }

    public class Location {
        @SerializedName("lat") public Double lat;
        @SerializedName("lng") public Double lng;
    }

    public class Photo {
        @SerializedName("photo_reference") public String reference;
    }
}
