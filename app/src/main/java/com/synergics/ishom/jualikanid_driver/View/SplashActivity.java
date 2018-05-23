package com.synergics.ishom.jualikanid_driver.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.synergics.ishom.jualikanid_driver.Controller.SessionManager;
import com.synergics.ishom.jualikanid_driver.Controller.Setting;
import com.synergics.ishom.jualikanid_driver.R;

public class SplashActivity extends AppCompatActivity {

    private int SPLAHTIMEOUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final SessionManager sessionManager = new SessionManager(getApplicationContext());
        final Setting setting = new Setting(this);

        if (setting.checkEnableGPS()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (sessionManager.isLoggedIn()){
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }else {
                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }

                }
            }, SPLAHTIMEOUT);
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (sessionManager.isLoggedIn()){
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }else {
                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }

                }
            }, SPLAHTIMEOUT);
        }
    }
}
