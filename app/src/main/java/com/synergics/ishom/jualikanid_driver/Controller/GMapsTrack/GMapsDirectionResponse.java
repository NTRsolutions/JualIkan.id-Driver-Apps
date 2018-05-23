package com.synergics.ishom.jualikanid_driver.Controller.GMapsTrack;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by asmarasusanto on 10/8/17.
 */

public class GMapsDirectionResponse {

    @SerializedName("routes") public List<Route> routes;

    public class Route {
        @SerializedName("legs") public List<Legs> legs;
    }

    public class Legs {
        @SerializedName("distance") public Distance distance;
        @SerializedName("duration") public Duration duration;

        //steps is polylineOptions
        @SerializedName("steps") public List<Steps> steps;
    }

    public class Distance {
        @SerializedName("text") public String distanceText;
        @SerializedName("value") public Double distanceValue;
    }

    public class Duration {
        @SerializedName("text") public String durationText;
        @SerializedName("value") public Double durationValue;
    }

    public class StartPoint {
        @SerializedName("lat") public Double lat;
        @SerializedName("lng") public Double lng;
    }

    public class EndPoint {
        @SerializedName("lat") public Double lat;
        @SerializedName("lng") public Double lng;
    }

    public class Steps {
        @SerializedName("polyline") public PolyLine polyLine;
        @SerializedName("distance") public Distance distance;
        @SerializedName("duration") public Duration duration;
        @SerializedName("start_location") public StartPoint startPoint;
        @SerializedName("end_location") public EndPoint endPoint;
    }

    public class PolyLine {
        @SerializedName("points") public String points;
    }
}
