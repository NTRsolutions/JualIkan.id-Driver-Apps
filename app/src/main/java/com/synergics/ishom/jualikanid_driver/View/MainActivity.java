package com.synergics.ishom.jualikanid_driver.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.synergics.ishom.jualikanid_driver.Controller.AppConfig;
import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiClient;
import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiInterface;
import com.synergics.ishom.jualikanid_driver.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_driver.Controller.SessionManager;
import com.synergics.ishom.jualikanid_driver.Controller.Setting;
import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseLogin;
import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseMainMenu;
import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseUpdateStatus;
import com.synergics.ishom.jualikanid_driver.R;
import com.synergics.ishom.jualikanid_driver.View.Delivery.DetailDeliveryFinishedActivity;
import com.synergics.ishom.jualikanid_driver.View.Delivery.TrackDetailDeliveryActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //sidebar
    private TextView txtUsername, txtSaldo;
    private ImageView imgUser;
    private View view;

    //db and session
    private SQLiteHandler db;
    private SessionManager sessionManager;

    private NavigationView navigationView;
    private Setting setting;

    //profile delivery
    private TextView userName, userEmail, userPhone, userStatus, userSaldo;
    private Switch statusSwitch;
    private ImageView imageUser;

    //delivery
    private TextView idDelivery, tanggalDeliery, waktuHoursDelivery, waktuDelivery, jarakDelivery, totalDelivery, delieryNoFound;
    private LinearLayout bgDelivery;

    //more button
    private TextView moreSaldo, morePengiriman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setting = new Setting(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new SQLiteHandler(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        view = navigationView.getHeaderView(0);

    }

    private void setContent() {

        //set profile content
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        userStatus = findViewById(R.id.txtStatus);
        userSaldo = findViewById(R.id.userSaldo);
        statusSwitch = findViewById(R.id.statusSwitch);

        imageUser = findViewById(R.id.userImage);

        userName.setText(db.getUser().driver_full_name);
        Picasso.with(getApplicationContext())
                .load(AppConfig.url +db.getUser().driver_image)
                .into(imageUser);

        //set delivery content
        idDelivery = findViewById(R.id.idDelivery);
        tanggalDeliery = findViewById(R.id.tanggalDelivery);
        waktuHoursDelivery = findViewById(R.id.waktuHoursDelivery);
        waktuDelivery = findViewById(R.id.waktuDelivery);
        jarakDelivery = findViewById(R.id.jarakDelivery);
        totalDelivery = findViewById(R.id.jumlahDelivery);
        delieryNoFound = findViewById(R.id.statusDelivery);

        bgDelivery = findViewById(R.id.bgRecentDelivery);

        morePengiriman = findViewById(R.id.btnMorePengiriman);
        moreSaldo = findViewById(R.id.btnMorePenghasilan);

        moreSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RiwayatSaldoActivity.class);
                startActivity(intent);
            }
        });

        morePengiriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RiwayatPengirimanActivity.class);
                startActivity(intent);
            }
        });

        statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    updateStatus(1);
                }else {
                    updateStatus(0);
                }
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences("ah_firebase", 0);
        String regId = pref.getString("regId", null);

        getMainMenuData();
    }

    private void updateStatus(int status) {
        String id = db.getUser().driver_id;

        final ProgressDialog pDialog = new ProgressDialog(this);
        // Showing progress dialog before Amaking http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        RequestBody idBody  = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody statusBody  = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(status));

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);
        Call call = apiInterface.updateStatus(idBody, statusBody);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                //mengambil data tracking
                if (response.isSuccessful()) {
                    ResponseUpdateStatus res = (ResponseUpdateStatus) response.body();
                    if (res.status){
                        Toast.makeText(MainActivity.this, res.message, Toast.LENGTH_SHORT).show();
                        if (res.driver_status == 1){
                            userStatus.setText(res.text_status);
                            userStatus.setTextColor(getResources().getColor(R.color.green));
                        }else if (res.driver_status == 0){
                            userStatus.setText(res.text_status);
                            userStatus.setTextColor(getResources().getColor(R.color.red));
                        }else {
                            userStatus.setText(res.text_status);
                            userStatus.setTextColor(getResources().getColor(R.color.blueFont));
                        }
                    }else {
                        Toast.makeText(MainActivity.this, "Gagal mengirim data dari server", Toast.LENGTH_SHORT).show();
                    }
                }
                pDialog.hide();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal mengabil data dari server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMainMenuData() {

        String id = db.getUser().driver_id;

        final ProgressDialog pDialog = new ProgressDialog(this);
        // Showing progress dialog before Amaking http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        RequestBody idBody  = RequestBody.create(MediaType.parse("text/plain"), id);

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);
        Call call = apiInterface.menu(idBody);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                //mengambil data tracking
                if (response.isSuccessful()) {
                    final ResponseMainMenu res = (ResponseMainMenu) response.body();
                    if (res.status){
                        if (res.data.driver.status == 1){
                            statusSwitch.setChecked(true);
                            userStatus.setText(res.data.driver.text_status);
                            userStatus.setTextColor(getResources().getColor(R.color.green));
                        }else if(res.data.driver.status == 0) {
                            userStatus.setText(res.data.driver.text_status);
                            userStatus.setTextColor(getResources().getColor(R.color.red));
                        }else {
                            statusSwitch.setEnabled(false);
                            userStatus.setText(res.data.driver.text_status);
                            userStatus.setTextColor(getResources().getColor(R.color.blueFont));
                        }

                        userPhone.setText(res.data.driver.phone);
                        userEmail.setText(res.data.driver.email);
                        userSaldo.setText("Rp. " + money(res.data.driver.saldo));
                        txtSaldo.setText("Rp. " + money(res.data.driver.saldo));

                        if (res.data.last_delivery != null){
                            bgDelivery.setVisibility(View.VISIBLE);
                            bgDelivery.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (res.data.last_delivery.status == 1){
                                        Intent intent = new Intent(getApplicationContext(), TrackDetailDeliveryActivity.class);
                                        intent.putExtra("delivery_id", res.data.last_delivery.id);
                                        startActivity(intent);
                                    }else {
                                        Intent intent = new Intent(getApplicationContext(), DetailDeliveryFinishedActivity.class);
                                        intent.putExtra("delivery_id", res.data.last_delivery.id);
                                        startActivity(intent);
                                    }
                                }
                            });
                            delieryNoFound.setVisibility(View.GONE);

                            idDelivery.setText(res.data.last_delivery.code);
                            tanggalDeliery.setText(getJam(res.data.last_delivery.date_time) +  " " + getTanggal(res.data.last_delivery.date_time));
//                            waktuHoursDelivery.setText(getStatus(res.data.last_delivery.date_time));
                            setStatus(res.data.last_delivery.status);
                            jarakDelivery.setText(res.data.last_delivery.distance);
                            waktuDelivery.setText(res.data.last_delivery.time);
                            totalDelivery.setText(res.data.last_delivery.order_count + " User");
                        }

                    }else {
                        Toast.makeText(MainActivity.this, "Gagal mengabil data dari server", Toast.LENGTH_SHORT).show();
                    }
                }
                pDialog.hide();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal mengabil data dari server", Toast.LENGTH_SHORT).show();
            }
        });

    }

//    private void acceptDelivery(){
//
//    }
//
//    private void sendNotification(){
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Intent intent = new Intent(getApplicationContext(), NotfiDetailDeliveryActivity.class);
//        intent.putExtras("delivery_id", getIntent().getExtras().getString("delivery_"))
//    }

    private void navHeaderContent() {
        txtUsername = view.findViewById(R.id.userName);
        txtSaldo = view.findViewById(R.id.userSaldo);
        imgUser = view.findViewById(R.id.userImage);

        ImageView imgProfile = view.findViewById(R.id.btnSetting);

        ResponseLogin.Data user = db.getUser();

        txtUsername.setText(user.driver_full_name);
        txtSaldo.setText("Rp. " + money(Integer.parseInt(user.driver_saldo)));

        Picasso.with(view.getContext())
                .load(AppConfig.url +user.driver_image)
                .into(imgUser);

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private String getTanggal(String date){
        String array[] = date.split("\\s+");
        String tanggal[] = array[0].split("-");
        return tanggal[2] + " " + bulanFormat(tanggal[1]) + " " + tanggal[0];
    }

    private String bulanFormat(String bulan){
        if (bulan.equals("01")){
            return "Januari";
        }else if (bulan.equals("02")){
            return "Februari";
        }else if (bulan.equals("03")){
            return "Maret";
        }else if (bulan.equals("04")){
            return "April";
        }else if (bulan.equals("05")){
            return "Mei";
        }else if (bulan.equals("06")){
            return "Juni";
        }else if (bulan.equals("07")){
            return "Juli";
        }else if (bulan.equals("08")){
            return "Agustus";
        }else if (bulan.equals("09")){
            return "September";
        }else if (bulan.equals("10")){
            return "Oktober";
        }else if (bulan.equals("11")){
            return "November";
        }else {
            return "Desember";
        }
    }

    private String getJam(String date){
        String array[] = date.split("\\s+");
        return array[1] + " WIB";
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.btn_home) {

        } else if (id == R.id.btn_riwayat_order) {
            Intent intent = new Intent(getApplicationContext(), RiwayatPengirimanActivity.class);
            startActivity(intent);
        } else if (id == R.id.btn_saldoku) {
            Intent intent = new Intent(getApplicationContext(), RiwayatSaldoActivity.class);
            startActivity(intent);
        } else if (id == R.id.btn_bantuan) {
            Intent intent = new Intent(getApplicationContext(), BantuanActivity.class);
            startActivity(intent);
        } else if (id == R.id.btn_logout) {
            Toast.makeText(getApplicationContext(), "Anda sedang logout!", Toast.LENGTH_SHORT).show();
            sessionManager.setLogin(false);
            db.hapusUser();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String money(int d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);;
        formatter .applyPattern("#,###");
        return formatter.format(d);
    }

    private int intToDp(float dpValue){
        float d = getApplicationContext().getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d); // margin in pixels
        return margin;
    }

    @Override
    protected void onResume() {
        setting.forceEnableGPSAcess();
        setting.getLocation();
        //setting content header navigation drawer
        navHeaderContent();

        //setting tampilan menu
        setContent();
        super.onResume();
    }

    public void setStatus(int status) {
        if (status == 1){
            waktuHoursDelivery.setText("Sedang Delivery");
            waktuHoursDelivery.setTextColor(getResources().getColor(R.color.blueFont));
        }else {
            waktuHoursDelivery.setText("Finished");
            waktuHoursDelivery.setTextColor(getResources().getColor(R.color.green));
        }
    }
}
