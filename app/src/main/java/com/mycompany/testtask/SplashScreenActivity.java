package com.mycompany.testtask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.concurrent.TimeUnit;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int TIMEOUT_SCREEN = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent nextIntent = new Intent(SplashScreenActivity.this, UsersScreenActivity.class);
                startActivity(nextIntent);
                finish();
            }
        }, TIMEOUT_SCREEN * 1000);
    }
}