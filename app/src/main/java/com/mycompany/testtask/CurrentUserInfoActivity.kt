package com.mycompany.testtask

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mycompany.testtask.data.User
import com.mycompany.testtask.databinding.ActivityCurrentUserInfoBinding
import com.mycompany.testtask.sharedprp.UserList

class CurrentUserInfoActivity : AppCompatActivity(), OnMapReadyCallback {
    private val KEY_URER_ID = "user_id"

    private lateinit var user : User

    private var openViewID : Byte = 0 // 0 - not, 1 - map, 2 - web

    private lateinit var binding : ActivityCurrentUserInfoBinding

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listOfUser = UserList(this)
        val bundle = intent.extras
        var userID : Int = 0

        if (bundle != null)
            userID = bundle.getInt(KEY_URER_ID, 0) - 1
            user = listOfUser.getCurUser(userID)
            userPrint()
            val mapFragment  = SupportMapFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.AdressMapView, mapFragment).commit()
            mapFragment.getMapAsync(this)
        if (userID == -1) {
            Toast.makeText(this, R.string.prob_user_data_non_avalible, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    fun Button(view: View?) { onBackPressed() }

    private fun userPrint(){
        val adr: String = user.address.city + ", " + user.address.street + ", " + user.address.suite
        binding.UserName.text = user.name
        binding.buttonAdress.text = adr
        binding.buttonEmail.text = user.email
        binding.buttonPhone.text = user.phone
        binding.buttonSite.text = user.website

        binding.buttonEmail.setOnClickListener {
            val email = user.email
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$email")
            intent.putExtra(Intent.EXTRA_SUBJECT, R.string.email_topic)
            intent.putExtra(Intent.EXTRA_TEXT, R.string.email_text)
            val emailApps = packageManager.queryIntentActivities(intent, 0)
            if (emailApps.size > 0) startActivity(intent)
            else Toast.makeText(this, R.string.prob_send_email_app, Toast.LENGTH_SHORT).show()
        }

        binding.buttonPhone.setOnClickListener {
            val phone = user.phone
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            val callApps = packageManager.queryIntentActivities(intent, 0)
            if (callApps.size > 0) startActivity(intent)
            else Toast.makeText(this, R.string.prob_dial_app, Toast.LENGTH_SHORT).show()
        }

        binding.buttonSite.setOnClickListener {
            if (openViewID.toInt() != 0) closeView()
            openViewID = 2
            val siteURL = user.website
            binding.WebSiteView.visibility = View.VISIBLE
            binding.buttonClose.visibility = View.VISIBLE
            binding.WebSiteView.loadUrl(siteURL)
        }

        binding.buttonAdress.setOnClickListener {
            if (openViewID.toInt() != 0) closeView()
            openViewID = 1
            binding.AdressMapViewLayout.visibility = View.VISIBLE
            binding.buttonClose.visibility = View.VISIBLE
        }

        binding.buttonClose.setOnClickListener {
            closeView()
        }
    }

    private fun closeView() {
        if (openViewID.toInt() == 1){
            binding.AdressMapViewLayout.visibility = View.INVISIBLE
            binding.buttonClose.visibility = View.INVISIBLE
        } else if (openViewID.toInt() == 2){
            binding.WebSiteView.visibility = View.INVISIBLE
            binding.buttonClose.visibility = View.INVISIBLE
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val lat = user.address.geo.lat.toDouble()
        val lng = user.address.geo.lng.toDouble()
        val adr: String = user.address.city + ", " + user.address.street + ", " + user.address.suite
        googleMap.addMarker(MarkerOptions().position(LatLng(lat, lng)).title(adr))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 10f))
    }
}