package com.synergics.ishom.jualikanid_driver.Model.TrackMaps;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by asmarasusanto on 10/2/17.
 */

public class Titik {
    private String titikName;
    private LatLng titikPoint;

    public Titik(String titikName, LatLng titikPoint) {
        this.titikName = titikName;
        this.titikPoint = titikPoint;
    }

    public String getTitikName() {
        return titikName;
    }

    public LatLng getTitikPoint() {
        return titikPoint;
    }
}
