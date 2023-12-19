package com.mycompany.testtask

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen);

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, UsersListActivity::class.java))
        }, TIMEOUT_SCREEN)
    }

    companion object{
        const val TIMEOUT_SCREEN:Long = 3000
    }
}