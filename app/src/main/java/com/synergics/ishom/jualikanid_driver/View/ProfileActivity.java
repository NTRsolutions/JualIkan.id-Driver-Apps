package com.synergics.ishom.jualikanid_driver.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.synergics.ishom.jualikanid_driver.Controller.AppConfig;
import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiClient;
import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiInterface;
import com.synergics.ishom.jualikanid_driver.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_driver.Controller.SessionManager;
import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseProfile;
import com.synergics.ishom.jualikanid_driver.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView driverName, driverEmail, driverPhone, driverWeight, driverSaldo;
    private ImageView driverImage;
    private LinearLayout btnSaldo, btnWeight, btnUbahProfile;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        driverName = findViewById(R.id.userName);
        driverEmail = findViewById(R.id.userEmail);
        driverPhone = findViewById(R.id.userPhone);
        driverWeight = findViewById(R.id.userWeight);
        driverSaldo = findViewById(R.id.userSaldo);

        driverImage = findViewById(R.id.userImage);
        btnSaldo = findViewById(R.id.bgSaldo);
        btnWeight = findViewById(R.id.bgWeight);
        btnUbahProfile = findViewById(R.id.bgUbahProfile);
        btnLogout = findViewById(R.id.btnLogout);

        btnSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RiwayatSaldoActivity.class);
                startActivity(intent);
            }
        });

        btnUbahProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UbahProfileActivity.class);
                startActivity(intent);
            }
        });

        btnWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UbahWeightActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SessionManager(getApplicationContext()).setLogin(false);
                new SQLiteHandler(getApplicationContext()).hapusUser();
                Intent intent = new Intent( getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        setToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfile();
    }

    private void getProfile(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody reUserId = RequestBody.create(MediaType.parse("text/plain"), new SQLiteHandler(getApplicationContext()).getUser().driver_id);

        Call call = apiInterface.profile(reUserId);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseProfile res= (ResponseProfile) response.body();
                    if (res.status){
                        Picasso.with(getApplicationContext()).load(AppConfig.url + res.profile.driver_image).into(driverImage);
                        driverName.setText(res.profile.driver_full_name);
                        driverEmail.setText(res.profile.driver_email);
                        driverPhone.setText(res.profile.driver_phone);
                        driverSaldo.setText("Rp. " + money(Integer.parseInt(res.profile.driver_saldo)));
                        driverWeight.setText(res.profile.driver_vehicle_weight+ " Kg");
                    }else {
                        Toast.makeText(ProfileActivity.this, "Gagal mengambil profile", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Failed to fetch into server!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.hide();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView toolbarTitle = findViewById(R.id.toolbarTittle);
        toolbarTitle.setText("Profile");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private String money(int d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);;
        formatter .applyPattern("#,###");
        return formatter.format(d);
    }
}
