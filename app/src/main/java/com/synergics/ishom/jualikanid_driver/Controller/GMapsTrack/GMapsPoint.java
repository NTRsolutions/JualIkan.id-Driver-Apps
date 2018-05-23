package com.synergics.ishom.jualikanid_driver.Controller.GMapsTrack;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by asmarasusanto on 2/10/18.
 */

public class GMapsPoint {
    private String name;
    private LatLng latLng;

    public GMapsPoint(String name, LatLng latLng) {
        this.name = name;
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
