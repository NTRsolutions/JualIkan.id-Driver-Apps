package com.synergics.ishom.jualikanid_driver.Controller;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.synergics.ishom.jualikanid_driver.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by asmarasusanto on 10/2/17.
 */

public class Setting {

    private Activity activity;
    private double lat;
    private double lng;
    private String alamat;

    public Setting(Activity activity) {
        this.activity = activity;
    }

    //check apakah gps sudah disetting atau belum
    public void forceEnableGPSAcess() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage((activity.getResources().getString(R.string.setting)))
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.i("Status ", "gps belum nyala");
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivityForResult(intent, 100);
                        }
                    }).show();
        } else {
            Log.i("Status ", "gps sudah nyala");
            Dexter
                    .withActivity(activity)
                    .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            getLocation();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        }
                    })
                    .check();
        }
    }

    public boolean checkEnableGPS() {
        boolean check = false;
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage((activity.getResources().getString(R.string.setting)))
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.i("Status ", "gps belum nyala");
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivityForResult(intent, 100);
                        }
                    }).show();

        } else {
            check = true;
            Log.i("Status ", "gps sudah nyala");
            Dexter
                    .withActivity(activity)
                    .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            getLocation();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        }
                    })
                    .check();
        }
        return check;
    }

    //mendapatkan Lokasi latitude anda sekarang
    public double getLatitude() {
        return lat;
    }

    //mendapatkan Lokasi longitude anda sekarang
    public double getLongitude() {
        return lng;
    }

    //mendapatkan alamat lokasi anda sekarang dengan parameter location
    public String getAdress(double lat, double lng) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address address = addresses.get(0);
            result.append(address.getAddressLine(0));
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        Log.i("getAdress from : ", lat + "" + lng + result.toString());
        return result.toString();
    }

    public String getAdress() {
        return alamat;
    }

    public String get() {
        return alamat;
    }

    public void getLocation() {
        LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        Log.i("Status ", "persiapan ambil lokasi");

        if (location != null){
            lat = location.getLatitude();
            lng = location.getLongitude();

            Log.i("Status lat ", lat + "");
            Log.i("Status lng ", lng + "");

            StringBuilder result = new StringBuilder();
            try {
                Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    result.append(address.getAddressLine(0));
                }
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }
            alamat = result.toString();

        }else{
            final LocationListener locationManager = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10*1000, 500, locationManager);
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 500, locationManager);

            StringBuilder result = new StringBuilder();
            try {
                Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    result.append(address.getAddressLine(0));
//                    result.append(address.getCountryName());
                }
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }
            alamat = result.toString();
        }
    }

}
