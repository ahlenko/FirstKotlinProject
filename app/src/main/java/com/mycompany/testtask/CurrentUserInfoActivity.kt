package com.mycompany.testtask

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mycompany.testtask.databinding.ActivityCurUserInfoBinding
import com.mycompany.testtask.fragments.CurrentUserInfoFragment

class CurrentUserInfoActivity : AppCompatActivity() {
    private val KEY_URER_ID = "user_id"

    private var userID : Int = 0

    private lateinit var binding : ActivityCurUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras

        if (bundle != null) userID = bundle.getInt(KEY_URER_ID, 0) - 1

        val infoFragment = CurrentUserInfoFragment()
        infoFragment.setPrintedUserID(userID)
        infoFragment.setFragmentTypeIsActivity(true)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, infoFragment)
            .commit()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) onBackPressed()

    }

    fun Button(view: View?) { onBackPressed() }
}