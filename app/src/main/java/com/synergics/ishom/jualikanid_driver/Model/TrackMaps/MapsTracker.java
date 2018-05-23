package com.synergics.ishom.jualikanid_driver.Model.TrackMaps;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by asmarasusanto on 10/2/17.
 */

public class MapsTracker  {

    //variabel penting dalam maps

    //variabel Multiple tracker
    private ArrayList<Titik> titiks = new ArrayList<>();

    private double maxLat = -999999999, minLat = 999999999;
    private double maxLng = -999999999, minLng = 999999999;
    private double midLat, midLng;

    public MapsTracker(){
    }

    public void addTitik(Titik titik){
        titiks.add(titik);
    }

    public void clear(){
        titiks.clear();
    }

//    public void setLocationAndDestionation(LatLng latLngAwal, LatLng latLngAkhir){
//        this.latLngAwal = latLngAwal;
//        this.latLngAkhir = latLngAkhir;
//        LatLng tmpLat = new LatLng((latLngAwal.latitude + latLngAkhir.latitude)/2, (latLngAwal.longitude + latLngAkhir.longitude)/2);
//        latLngAvarage = tmpLat;
//        setUrl();
//    }

    public void getMaxMin(){
        for (int i = 0; i < titiks.size(); i++){
            if (titiks.get(i).getTitikPoint().latitude > maxLat){
                maxLat = titiks.get(i).getTitikPoint().latitude;
            }
            if (titiks.get(i).getTitikPoint().latitude < minLat){
                minLat = titiks.get(i).getTitikPoint().latitude;
            }
            if (titiks.get(i).getTitikPoint().longitude > maxLng){
                maxLng = titiks.get(i).getTitikPoint().longitude;
            }
            if (titiks.get(i).getTitikPoint().longitude < minLng){
                minLng = titiks.get(i).getTitikPoint().longitude;
            }
        }
    }

    public LatLng getMid(){
        midLat = (minLat + maxLat)/2;
        midLng = (minLng + maxLng)/2;

        return new LatLng(midLat, midLng);
    }

    public String getOrigin () {
        return titiks.get(0).getTitikPoint().latitude +","+titiks.get(0).getTitikPoint().longitude;
    }

    public String getDestionation () {
        return titiks.get(1).getTitikPoint().latitude +","+titiks.get(1).getTitikPoint().longitude;
    }

    public String getWaypoint(){
        String track = "";
        int jumlah = titiks.size();
        if (jumlah != 2){
            for (int i = 0; i < jumlah; i++){
                if (i != 0 && i != 1){
                    track = track + "via:" + titiks.get(i).getTitikPoint().latitude +","+titiks.get(i).getTitikPoint().longitude + "|";
                }
            }
        }
        return track;
    }

    public ArrayList<Titik> getTitiks(){
        return titiks;
    }
}
