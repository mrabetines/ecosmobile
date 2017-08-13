package com.vayetek.ecosapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vayetek.ecosapp.R;
import com.vayetek.ecosapp.Utils;


public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.splash_screen_layout);

        super.onCreate(savedInstanceState);
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < 3000) {

                        sleep(500);
                        waited += 500;
                    }
                    if (Utils.isUserAuthenticated(SplashScreenActivity.this)) {
                        Utils.token = Utils.getAuthorization(SplashScreenActivity.this);
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SplashScreenActivity.this, UniversityAuthActivity.class));
                    }
                    SplashScreenActivity.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        };
        splashThread.start();
    }
}
