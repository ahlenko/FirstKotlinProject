package com.mycompany.testtask

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mycompany.testtask.databinding.ActivityCurUserInfoBinding
import com.mycompany.testtask.fragments.CurrentUserInfoFragment

class CurrentUserInfoActivity : AppCompatActivity() {
    private var userID : Int = 0

    private lateinit var binding : ActivityCurUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras

        if (bundle != null) userID = bundle.getInt(KEY_USER_ID, 0) - 1

        val infoFragment = CurrentUserInfoFragment()
        infoFragment.setPrintedUserID(userID)
        infoFragment.setFragmentTypeIsActivity(true)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_main, infoFragment)
            .commit()
    }

    @SuppressLint("ChromeOsOnConfigurationChanged")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) this.finish()
    }

    companion object{
        const val KEY_USER_ID = "user_id"
    }
}