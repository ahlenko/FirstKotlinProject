package com.mycompany.testtask

import android.content.Intent
import android.content.res.Configuration
import android.icu.number.IntegerWidth
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mycompany.testtask.databinding.ActivityUsersListBinding
import com.mycompany.testtask.fragments.CurrentUserInfoFragment
import com.mycompany.testtask.fragments.UserListFragment

class UsersListActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUsersListBinding

    private val buttonToInfoScreen = View.OnClickListener { v ->
        val nextIntent = Intent(
            this@UsersListActivity,
            CurrentUserInfoActivity::class.java )
        val bundle = Bundle()
        val userID = Integer.parseInt(v.tag.toString()) - 1
        bundle.putInt(KEY_USER_ID, userID)
        nextIntent.putExtras(bundle)
        startActivity(nextIntent)
    }

    private val buttonShowUserInfo = View.OnClickListener { v ->
        val userID = Integer.parseInt(v.tag.toString()) - 1
        val infoFragment = CurrentUserInfoFragment()
        infoFragment.setPrintedUserID(userID-1)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_right, infoFragment)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            handleOrientationChange(resources.configuration.orientation)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        handleOrientationChange(newConfig.orientation)
    }

    private fun handleOrientationChange(orientation: Int) {
        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                val listFragment = UserListFragment()
                listFragment.setOnClickListener(buttonToInfoScreen)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, listFragment)
                    .commit()

                binding.fragmentContainerMain.visibility = View.VISIBLE
                binding.fragmentContainerLeft.visibility = View.GONE
                binding.fragmentContainerRight.visibility = View.GONE
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                val listFragment = UserListFragment()
                val infoFragment = CurrentUserInfoFragment()
                listFragment.setOnClickListener(buttonShowUserInfo)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_left, listFragment)
                    .replace(R.id.fragment_container_right, infoFragment)
                    .commit()

                binding.fragmentContainerMain.visibility = View.GONE
                binding.fragmentContainerLeft.visibility = View.VISIBLE
                binding.fragmentContainerRight.visibility = View.VISIBLE
            }
        }
    }

    companion object{
        const val KEY_USER_ID = "user_id"
    }
}


