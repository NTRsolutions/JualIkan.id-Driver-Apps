package com.synergics.ishom.jualikanid_driver.Controller.GMapsTrack;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiClient;
import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by asmarasusanto on 2/10/18.
 */

public class GMapsTracking {

    /*
    Crated by : ishom14     => https://gitlab.com/ishom14
    Library Used :
        - Google Maps API   => https://developers.google.com/maps/
        - Retrofit 2.*      => http://square.github.io/retrofit/
        - OKHttp3           => https://github.com/square/okhttp
    Purpose : Google Maps Tracking Location Using Retrofit and OKHttpConection
    Note :
        - Prepare ApiClient and ApiInterface to dial connection with google maps APi
    */

    private ArrayList<GMapsPoint> points = new ArrayList<>();
    private ArrayList<LatLng> trackPoints = new ArrayList<>();
    private Context context;
    private GoogleMap googleMap;

    private PolylineOptions lines = null;
    private Polyline poly;
    private int polyLineColor = Color.BLUE;
    private int polyLineWidth = 8;

    private BitmapDescriptor startMarker = null;
    private BitmapDescriptor endMarker = null;

    private double maxLat = -999999999, maxLng = -999999999;
    private double minLat = 999999999, minLng = 999999999;
    private double midLat, midLng;

    private double distance;

    public GMapsTracking(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
    }

    public GMapsTracking(Context context) {
        this.context = context;
    }

    public void addGMapsPoint(GMapsPoint point){
        points.add(point);
    }

    public ArrayList<GMapsPoint> getPoints() {
        return points;
    }

    public GMapsPoint getStartPoint(){
        return points.get(0);
    }

    public GMapsPoint getLastPoint(){
        return points.get(points.size()-1);
    }

    public GMapsPoint getIndexPoint(int index){
        return points.get(index);
    }

    public void setPolyLineColor (int color){
        polyLineColor = color;
    }

    public void setPolyLineWidth (int width){
        polyLineWidth = width;
    }

    public void setStartMarker (int startMarker){
        this.startMarker = bitmapDescriptorFromVector(context, startMarker);
    }

    public void setEndMarker(int endMarker){
        this.endMarker = bitmapDescriptorFromVector(context, endMarker);
    }

    public void getMaxMin(ArrayList<GMapsPoint> point){
        for (int i = 0; i < point.size(); i++){
            if (point.get(i).getLatLng().latitude > maxLat){
                maxLat = point.get(i).getLatLng().latitude;
            }
            if (point.get(i).getLatLng().latitude < minLat){
                minLat = point.get(i).getLatLng().latitude;
            }
            if (point.get(i).getLatLng().longitude > maxLng){
                maxLng = point.get(i).getLatLng().longitude;
            }
            if (point.get(i).getLatLng().longitude < minLng){
                minLng = point.get(i).getLatLng().longitude;
            }
        }
    }

    public LatLng getMid(){
        midLat = (minLat + maxLat)/2;
        midLng = (minLng + maxLng)/2;

        return new LatLng(midLat, midLng);
    }

    public void trackDirection(LatLng origin, LatLng destionation, String mode){

        trackPoints.clear();

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.show();

        String originText = origin.latitude + "," + origin.longitude;
        String destinationText = destionation.latitude + "," + destionation.longitude;

        ApiInterface apiInterface = ApiClient.mapsApi().create(ApiInterface.class);
        Call call = apiInterface.getDirection(originText, destinationText, true, mode);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()){

                    GMapsDirectionResponse directionResponse = (GMapsDirectionResponse) response.body();
                    List<GMapsDirectionResponse.Route> routes = directionResponse.routes;

                    for (GMapsDirectionResponse.Route route : routes){

                        List<GMapsDirectionResponse.Legs> legses = route.legs;

                        for (GMapsDirectionResponse.Legs legs : legses){

                            List<GMapsDirectionResponse.Steps> stepses = legs.steps;

                            for (GMapsDirectionResponse.Steps steps : stepses){

                                List poly = decodePoly(steps.polyLine.points);
                                lines = new PolylineOptions();

                                for (int i = 0; i < poly.size(); i++){
                                    trackPoints.add(new LatLng(((LatLng)poly.get(i)).latitude, ((LatLng)poly.get(i)).longitude));
                                }

                                lines.addAll(trackPoints);
                                lines.width(polyLineWidth);
                                lines.color(polyLineColor);
                                lines.geodesic(true);
                            }

                        }

                    }
                }
                displayPolyLineAndDrawMarker();
                dialog.hide();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

    }

    public void countDistance(LatLng origin, LatLng destionation, String mode){

        trackPoints.clear();

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.show();

        String originText = origin.latitude + "," + origin.longitude;
        String destinationText = destionation.latitude + "," + destionation.longitude;

        ApiInterface apiInterface = ApiClient.mapsApi().create(ApiInterface.class);
        Call call = apiInterface.getDirection(originText, destinationText, true, mode);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()){

                    GMapsDirectionResponse directionResponse = (GMapsDirectionResponse) response.body();
                    List<GMapsDirectionResponse.Route> routes = directionResponse.routes;

                    for (GMapsDirectionResponse.Route route : routes){

                        List<GMapsDirectionResponse.Legs> legses = route.legs;

                        for (GMapsDirectionResponse.Legs legs : legses){

                            distance = legs.distance.distanceValue;
                            Log.i("Distance " , distance + "");

                        }

                    }
                }
                dialog.hide();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });

    }

    public double getDistance(){
        return this.distance;
    }

    private void displayPolyLine(){
        getMaxMin(points);
        LatLng avarage = getMid();

        poly = googleMap.addPolyline(lines);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(avarage));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
    }

    private void displayPolyLineAndDrawMarker(){
        if (startMarker == null && endMarker == null){
            googleMap.addMarker(new MarkerOptions().position(points.get(0).getLatLng()).icon(startMarker));
            googleMap.addMarker(new MarkerOptions().position(points.get(1).getLatLng()).icon(endMarker));
        }else {
            googleMap.addMarker(new MarkerOptions().position(points.get(0).getLatLng()));
            googleMap.addMarker(new MarkerOptions().position(points.get(1).getLatLng()));
        }

        getMaxMin(points);
        LatLng avarage = getMid();
        poly = googleMap.addPolyline(lines);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(avarage));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f));
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


}
