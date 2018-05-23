package com.synergics.ishom.jualikanid_driver.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiClient;
import com.synergics.ishom.jualikanid_driver.Controller.RetroConfig.ApiInterface;
import com.synergics.ishom.jualikanid_driver.Controller.SQLiteHandler;
import com.synergics.ishom.jualikanid_driver.Controller.SessionManager;
import com.synergics.ishom.jualikanid_driver.Model.Retrofit.ResponseLogin;
import com.synergics.ishom.jualikanid_driver.R;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtPhone, edtPassword;
    private Button btnLogin;
    private SessionManager manager;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        manager = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        edtPhone = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);

        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });
    }

    private void checkLogin() {
        String phone, password;
        phone = edtPhone.getText().toString().trim();
        password = edtPassword.getText().toString().trim();

        if (!phone.isEmpty() && !password.isEmpty()){
            sendLogin(phone, password);
        }else {
            Toast.makeText(getApplicationContext(), "Pastikan anda memasukan phone dan password", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendLogin(String phone, String password) {
        String  device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.jualikanService().create(ApiInterface.class);

        RequestBody rePhone = RequestBody.create(MediaType.parse("text/plain"), phone);
        RequestBody rePassword = RequestBody.create(MediaType.parse("text/plain"), password);
        RequestBody reDevice = RequestBody.create(MediaType.parse("text/plain"), device_id);

        Call call = apiInterface.login(rePhone, rePassword, reDevice);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()){
                    ResponseLogin res = (ResponseLogin) response.body();

                    if (res.status){
                        Toast.makeText(getApplicationContext(), "Login Berhasil !", Toast.LENGTH_SHORT).show();
                        manager.setLogin(true);
                        db.addUser(res.data);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(), "Password atau Nomer Telephone tidak terdaftar !", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(), "Register failed!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.hide();
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }
}
