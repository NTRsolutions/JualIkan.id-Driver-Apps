package com.synergics.ishom.jualikanid_driver.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.synergics.ishom.jualikanid_driver.Controller.AppConfig;
import com.synergics.ishom.jualikanid_driver.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_driver.Controller.SessionManager;
import com.synergics.ishom.jualikanid_driver.Controller.Setting;
import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseLogin;
import com.synergics.ishom.jualikanid_driver.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


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

        //setting content header navigation drawer
        navHeaderContent();

        //setting tampilan menu
    }

    private void navHeaderContent() {
        txtUsername = view.findViewById(R.id.userName);
        txtSaldo = view.findViewById(R.id.userSaldo);
        imgUser = view.findViewById(R.id.userImage);

        ResponseLogin.Data user = db.getUser();

        txtUsername.setText(user.driver_full_name);
        txtSaldo.setText("Rp. " + money(Integer.parseInt(user.driver_saldo)));

        Picasso.with(view.getContext())
                .load(AppConfig.url +user.driver_image)
                .into(imgUser);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.keranjang) {
//            Intent intent = new Intent(getApplicationContext(), KeranjangActivity.class);
//            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.btn_home) {

        } else if (id == R.id.btn_riwayat_order) {
//            Intent intent = new Intent(getApplicationContext(), RiwayatOrderActivity.class);
//            startActivity(intent);
        } else if (id == R.id.btn_saldoku) {
//            Intent intent = new Intent(getApplicationContext(), RiwayatOrderActivity.class);
//            startActivity(intent);
        } else if (id == R.id.btn_bantuan) {
//            Intent intent = new Intent(getApplicationContext(), BantuanActivity.class);
//            startActivity(intent);
        } else if (id == R.id.btn_logout) {
            Toast.makeText(getApplicationContext(), "Anda sedang logout!", Toast.LENGTH_SHORT).show();
            sessionManager.setLogin(false);
            db.hapusUser();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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
        super.onResume();
    }
}
