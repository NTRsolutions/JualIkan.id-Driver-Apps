package com.synergics.ishom.jualikanid_driver.View.Delivery;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.synergics.ishom.jualikanid_driver.Controller.AppConfig;
import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiClient;
import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiInterface;
import com.synergics.ishom.jualikanid_driver.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_driver.Controller.Setting;
import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseDetailDelivery;
import com.synergics.ishom.jualikanid_driver.Model.TrackMaps.Direction;
import com.synergics.ishom.jualikanid_driver.Model.TrackMaps.MapsTracker;
import com.synergics.ishom.jualikanid_driver.Model.TrackMaps.Titik;
import com.synergics.ishom.jualikanid_driver.R;

import net.idik.lib.slimadapter.SlimAdapter;
import net.idik.lib.slimadapter.SlimInjector;
import net.idik.lib.slimadapter.viewinjector.IViewInjector;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailDeliveryFinishedActivity extends AppCompatActivity implements OnMapReadyCallback, LocationSource.OnLocationChangedListener, LocationListener {

    private TextView txtJarakPengiriman, txtWaktuPengiriman, txtBiayaPengiriman;
    private Button btnFinish;

    private GoogleMap mMap;
    private LatLngBounds.Builder bounds;
    private Marker myPositionMarker;
    private LocationManager locationManager;

    private Setting setting;

    private ArrayList<LatLng> points = new ArrayList<>();

    private TextView toolbarTitle, deliveryId, deliveryStatus;
    private HashMap<Marker, ResponseDetailDelivery.Order> listOrder = new HashMap<>();

    private MapsTracker mapsTracker;

    private PolylineOptions lineOptions = null;
    private Polyline poly;
    private String provider;

    private int counter = 0;

    private DatabaseReference firebaseDb = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_delivery);

        mapsTracker = new MapsTracker();

        txtJarakPengiriman = findViewById(R.id.jarakDelivery);
        txtWaktuPengiriman = findViewById(R.id.waktuDelivery);
        txtBiayaPengiriman = findViewById(R.id.biayaDelivery);
        deliveryId = findViewById(R.id.idDelivery);
        deliveryStatus = findViewById(R.id.statusDelivery);

        bounds = new LatLngBounds.Builder();

        setting = new Setting(this);
        setting.forceEnableGPSAcess();
        setting.getLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setToolbar();
    }

    private void completeDelivery() {
        Toast.makeText(this, "Delivery Finished !!", Toast.LENGTH_SHORT).show();
        finishNotification();
    }

    private void finishNotification() {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


    private void getDetailPengiriman() {

        String id_delivery = getIntent().getExtras().getString("delivery_id");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        Call call = apiInterface.detailDelivery(id_delivery);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()) {
                    ResponseDetailDelivery res = (ResponseDetailDelivery) response.body();

                    if (res.status) {
                        toolbarTitle.setText(res.data.code);
                        deliveryId.setText(res.data.code);

                        txtJarakPengiriman.setText(res.data.distance.text);
                        txtWaktuPengiriman.setText(res.data.time.text);
                        txtBiayaPengiriman.setText("Rp. " + money(res.data.biaya.value));

                        setStatusColor(res.data.status, deliveryStatus);

                        mapsTracker.addTitik(new Titik("", new LatLng(Double.parseDouble(res.data.koperasi.lat), Double.parseDouble(res.data.koperasi.lng))));
                        mapsTracker.addTitik(new Titik("", new LatLng(Double.parseDouble(res.data.koperasi.lat), Double.parseDouble(res.data.koperasi.lng))));

                        for (ResponseDetailDelivery.Order order : res.data.orders) {
                            Marker marker = insialisasiMarkerDrawable(order);
                            listOrder.put(marker, order);
                            mapsTracker.addTitik(new Titik("", new LatLng(Double.parseDouble(order.lokasi.lokasi_lat), Double.parseDouble(order.lokasi.lokasi_lng))));
                        }

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(res.data.koperasi.lat), Double.parseDouble(res.data.koperasi.lng)))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_company)));
                        bounds.include(new LatLng(Double.parseDouble(res.data.koperasi.lat), Double.parseDouble(res.data.koperasi.lng)));

                        LatLngBounds latLngBounds = bounds.build();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));

                    } else {
                        Toast.makeText(getApplicationContext(), res.message, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Gagal konerksi ke server!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.hide();
                String origin = mapsTracker.getOrigin();
                String destionation = mapsTracker.getDestionation();
                String waypoint = mapsTracker.getWaypoint();

                trackingDirection(origin, destionation, waypoint, "DRIVING");
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    private void trackingDirection(String origin, String destionation, String waypoint, String mode) {

        final ProgressDialog pDialog = new ProgressDialog(this);
        // Showing progress dialog before Amaking http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        ApiInterface apiInterface = ApiClient.mapsApi().create(ApiInterface.class);
        Call call = apiInterface.getDirection(origin, destionation, waypoint, false, mode);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                //mengambil data tracking
                if (response.isSuccessful()) {
                    Direction direction = (Direction) response.body();
                    List<Direction.Route> routes = direction.routes;
                    for (Direction.Route route : routes) {

                        List<Direction.Legs> legs = route.legs;

                        for (Direction.Legs leg : legs) {

                            //mengambil steps
                            List<Direction.Steps> steps = leg.steps;

                            for (Direction.Steps step : steps) {

                                List poly = decodePoly(step.polyLine.points);

                                lineOptions = new PolylineOptions();

                                for (int l = 0; l < poly.size(); l++) {
                                    points.add(new LatLng(((LatLng) poly.get(l)).latitude, ((LatLng) poly.get(l)).longitude));
                                    bounds.include(points.get(l));
                                }

                                lineOptions.addAll(points);
                                lineOptions.width(8);
                                lineOptions.color(Color.BLUE);
                                lineOptions.geodesic(true);

                            }
                        }
                    }
                }

                //selesai mengambil data tracking
                displayPolyline();

                pDialog.hide();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    private void displayPolyline() {
        poly = mMap.addPolyline(lineOptions);
        LatLngBounds latLngBounds = bounds.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
    }

    private Marker insialisasiMarkerDrawable(ResponseDetailDelivery.Order data) {

        View iconView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_marker, null);

        ImageView imageLokasi = iconView.findViewById(R.id.image);
        Picasso.with(iconView.getContext())
                .load(AppConfig.url + data.user.image)
                .resize(100, 100)
                .centerCrop()
                .into(imageLokasi);

        iconView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        iconView.layout(0, 0, iconView.getMeasuredWidth(), iconView.getMeasuredHeight());
        iconView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(iconView.getMeasuredWidth(), iconView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = iconView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        iconView.draw(canvas);

        Log.d("Dialog : ", AppConfig.url + data.user.image);

        LatLng position = new LatLng(Double.parseDouble(data.lokasi.lokasi_lat), Double.parseDouble(data.lokasi.lokasi_lng));

        bounds.include(position);

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

        return marker;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbarTitle = findViewById(R.id.toolbarTittle);
    }


    private String money(int d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###");
        return formatter.format(d);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setting.forceEnableGPSAcess();
        setting.getLocation();

        mMap = googleMap;

        LatLng position = new LatLng(setting.getLatitude(), setting.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                ResponseDetailDelivery.Order data = listOrder.get(marker);
                if (data != null) {
                    displayDialog(data);
                }
                return false;
            }
        });

        getDetailPengiriman();
        registerLocationUpdate();
    }

    private void displayDialog(final ResponseDetailDelivery.Order data) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_detail_order);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //detail user
        ImageView userImage = dialog.findViewById(R.id.userImage);
        TextView userName = dialog.findViewById(R.id.userName);
        TextView userEmail = dialog.findViewById(R.id.userEmail);
        TextView userPhone = dialog.findViewById(R.id.userPhone);

        userName.setText(data.user.name);
        userEmail.setText(data.user.email);
        userPhone.setText(data.user.phone);
        Picasso.with(dialog.getContext())
                .load(AppConfig.url + data.user.image)
                .into(userImage);

        LinearLayout btnCall = dialog.findViewById(R.id.btnCall);
        LinearLayout btnMessage = dialog.findViewById(R.id.btnMessage);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + data.user.phone));
                startActivity(intent);
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.fromParts("sms", data.user.phone, null));
                startActivity(intent);
            }
        });

        //detail alamat
        TextView txtAlamat = dialog.findViewById(R.id.txtAlamat);
        txtAlamat.setText(data.lokasi.lokasi_name);

        //daftar keranjang
        RecyclerView recyclerView = dialog.findViewById(R.id.recycle);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        SlimAdapter slimAdapter = SlimAdapter.create()
                .register(R.layout.layout_keranjang_item, new SlimInjector<ResponseDetailDelivery.Order.CartDetail>() {
                    @Override
                    public void onInject(final ResponseDetailDelivery.Order.CartDetail data, IViewInjector injector) {
                        injector.text(R.id.nama, data.name + " (" + data.qty + "Kg)")
                                .text(R.id.harga, "Rp. " + money(data.price));
                    }
                })
                .updateData(data.cart.items)
                .attachTo(recyclerView);

        slimAdapter.notifyDataSetChanged();
        TextView total = dialog.findViewById(R.id.totalKeranjang);
        total.setText("Rp. " + money(data.cart.total));

        //btnClose
        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });

        dialog.show();
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private BitmapDescriptor getDriverMarkerBitmap() {
        View iconView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_marker_driver, null);
        iconView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        iconView.layout(0, 0, iconView.getMeasuredWidth(), iconView.getMeasuredHeight());
        iconView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(iconView.getMeasuredWidth(), iconView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = iconView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        iconView.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void registerLocationUpdate() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setAccuracy(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 1L, 1f, this);

        Location oldLocation = locationManager.getLastKnownLocation(provider);
        if (myPositionMarker == null && oldLocation != null){
            String regId = new SQLiteHandler(getApplicationContext()).getUser().driver_id;
            firebaseDb.child("Tracking").child(regId).setValue(oldLocation);
            firebaseDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            myPositionMarker = mMap.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(getDriverMarkerBitmap())
                    .anchor(0.5f, 0.5f)
                    .position(new LatLng(oldLocation.getLatitude(), oldLocation.getLongitude()))
            );

        }

        animateMarker(myPositionMarker, oldLocation);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null){
            return;
        }

        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        firebaseDb.child("Tracking").child(db.getUser().driver_id).setValue(location);
        firebaseDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("onLocationChanged: ", location.getLatitude() + "," + location.getLongitude());

        if (myPositionMarker == null){
            myPositionMarker = mMap.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(getDriverMarkerBitmap())
                    .anchor(0.5f, 0.5f)
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
            );
        }

        animateMarker(myPositionMarker, location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("onStatusChanged: " , "marker status has been update");
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void setStatusColor(int status, TextView textView){
        if (status == 2){
            textView.setText("Finished");
            textView.setTextColor(getResources().getColor(R.color.green));
        }else {
            textView.setText("Sedang Delivery");
            textView.setTextColor(getResources().getColor(R.color.blueFont));
        }
    }

    private void animateMarker(final Marker marker, final Location location) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final double startRotation = marker.getRotation();
        final long duration = 500;

        final LinearInterpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed/duration);

                double lat = t * location.getLatitude() + (1-t) * startLatLng.latitude;
                double lng = t * location.getLongitude() + (1-t) * startLatLng.longitude;

                float rotation = (float) (t * location.getBearing() + (1 - t) * startRotation);

                marker.setPosition(new LatLng(lat, lng));
                marker.setRotation(rotation);

                if ( t < 1.0){
                    handler.postDelayed(this, 16);
                }
            }
        });

    }
}