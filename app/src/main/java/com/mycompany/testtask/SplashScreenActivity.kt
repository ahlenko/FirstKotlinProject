package com.mycompany.testtask

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.mycompany.testtask.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private val TIMEOUT_SCREEN:Long = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen);

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, UsersListActivity::class.java))
        }, TIMEOUT_SCREEN)
    }
}