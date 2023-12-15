package com.mycompany.testtask

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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mycompany.testtask.data.User
import com.mycompany.testtask.databinding.ActivityCurrentUserInfoBinding
import com.mycompany.testtask.sharedprp.DasCashed
import com.mycompany.testtask.usersdata.UserList

class CurrentUserInfoActivity : AppCompatActivity(), OnMapReadyCallback {
    private val KEY_URER_ID = "user_id"

    private var openViewID : Byte = 0
    //0 - not open
    //2 - openWeb
    //1 - openMap
    private var userID : Int = 0

    private lateinit var curUser : User

    private lateinit var binding : ActivityCurrentUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listOfUser = UserList(this)
        val dasCashed = DasCashed(this)
        val bundle = intent.extras

        if (bundle != null)
            userID = bundle.getInt(KEY_URER_ID, 0)
        if (userID == 0) {
            Toast.makeText(this, "Помилка. Дані для користувача недоступні", Toast.LENGTH_SHORT).show()
            finish()
        }

        val userPrintThread : Thread = Thread {
            val user : User = listOfUser.getCurUser(userID)
            val adr: String = user.address.city + ", " + user.address.street + ", " + user.address.suite
            runOnUiThread(Runnable {
                curUser = user
                binding.UserName.text = user.name
                binding.buttonAdress.text = adr
                binding.buttonEmail.text = user.email
                binding.buttonPhone.text = user.phone
                binding.buttonSite.text = user.website

                binding.buttonEmail.setOnClickListener {
                    val email = user.email
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:$email")
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Тема вашого листа")
                    intent.putExtra(Intent.EXTRA_TEXT, "Ваше повідомлення тут")
                    val emailApps = packageManager.queryIntentActivities(intent, 0)
                    if (emailApps.size > 0) startActivity(intent)
                    else Toast.makeText(this, "Помилка. Додаток для надсилання email відсутній", Toast.LENGTH_SHORT).show()
                }

                binding.buttonPhone.setOnClickListener {
                    val phone = user.phone
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:$phone")
                    val callApps = packageManager.queryIntentActivities(intent, 0)
                    if (callApps.size > 0) startActivity(intent)
                    else Toast.makeText(this, "Помилка. Додаток для виконання дзвінка відсутній", Toast.LENGTH_SHORT).show()
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
                    binding.AdressMapView.visibility = View.VISIBLE
                    binding.buttonClose.visibility = View.VISIBLE
                    createMapView()
                }

                binding.buttonClose.setOnClickListener {
                    closeView()
                }
            })
        }

        if (dasCashed.isCashed() && !isInternetAvailable(this)){
            Toast.makeText(this, "Інтернет підключення відсутнє, відображено кешовані дані", Toast.LENGTH_SHORT).show()
            userPrintThread.start()
            userPrintThread.join()
        } else {
            userPrintThread.start()
            userPrintThread.join()
        }
    }

    fun Button(view: View?) { onBackPressed() }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    private fun createMapView() {
        binding.AdressMapView.visibility = View.VISIBLE
        binding.AdressMapView.getMapAsync(this)
    }

    private fun closeView() {
        if (openViewID.toInt() == 1){
            binding.AdressMapView.visibility = View.INVISIBLE
            binding.buttonClose.visibility = View.INVISIBLE
        }else if (openViewID.toInt() == 2){
            binding.WebSiteView.visibility = View.INVISIBLE
            binding.buttonClose.visibility = View.INVISIBLE
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val lat = curUser.address.geo.lat.toDouble()
        val lng = curUser.address.geo.lng.toDouble()
        val adr: String = curUser.address.city + ", " + curUser.address.street + ", " + curUser.address.suite
        val markerOptions = MarkerOptions().position(LatLng(lat, lng)).title(adr)
        googleMap.addMarker(markerOptions)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lng)))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
    }
}