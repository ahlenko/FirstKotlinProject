package com.mycompany.testtask.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mycompany.testtask.R
import com.mycompany.testtask.data.User
import com.mycompany.testtask.databinding.FragmentUsersInfoBinding
import com.mycompany.testtask.sharedprp.UserList

class CurrentUserInfoFragment : Fragment(), OnMapReadyCallback {

    private lateinit var user : User

    private var userID : Int = -2

    private var fragmentTypeIsActivity : Boolean = false

    private var openViewID : Byte = 0 // 0 - not, 1 - map, 2 - web

    private lateinit var binding : FragmentUsersInfoBinding

    private lateinit var listOfUser : UserList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listOfUser = UserList(requireActivity().baseContext)
        if (userID == -1) {
            binding.screenContentLayout.visibility  = View.INVISIBLE
            Toast.makeText(requireActivity().baseContext, R.string.prob_user_data_non_avalible, Toast.LENGTH_SHORT).show()
            if (fragmentTypeIsActivity) requireActivity().finish()
        } else if (userID == -2) {
            binding.screenContentLayout.visibility  = View.INVISIBLE
            binding.ifUserNoSelectedTitle.visibility = View.VISIBLE
        } else {
            if (!fragmentTypeIsActivity) binding.backButton.visibility = View.INVISIBLE
            user = listOfUser.getCurUser(userID)
            initializeUserInfo()
            val mapFragment  = SupportMapFragment.newInstance()
            requireActivity().supportFragmentManager.beginTransaction().add(R.id.map_view_element, mapFragment).commit()
            mapFragment.getMapAsync(this)
        }
        binding.backButton.setOnClickListener { requireActivity().finish() }
    }

    fun setFragmentTypeIsActivity(type: Boolean) {
        fragmentTypeIsActivity = type
    }

    fun setPrintedUserID(printedUserID: Int) {
        userID = printedUserID
    }

    private fun initializeUserInfo() {
        val adr: String = user.address.city + ", " + user.address.street + ", " + user.address.suite
        binding.userNameView.text = user.name
        binding.showMapButton.text = adr
        binding.userEmailViewButton.text = user.email
        binding.userPhoneViewButton.text = user.phone
        binding.showWebButton.text = user.website

        binding.userEmailViewButton.setOnClickListener {
            val email = user.email
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$email")
            intent.putExtra(Intent.EXTRA_SUBJECT, R.string.email_topic)
            intent.putExtra(Intent.EXTRA_TEXT, R.string.email_text)
            val emailApps = requireActivity().packageManager.queryIntentActivities(intent, 0)
            if (emailApps.size > 0) startActivity(intent)
            else Toast.makeText(requireActivity().baseContext, R.string.prob_send_email_app, Toast.LENGTH_SHORT).show()
        }

        binding.userPhoneViewButton.setOnClickListener {
            val phone = user.phone
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            val callApps = requireActivity().packageManager.queryIntentActivities(intent, 0)
            if (callApps.size > 0) startActivity(intent)
            else Toast.makeText(requireActivity().baseContext, R.string.prob_dial_app, Toast.LENGTH_SHORT).show()
        }

        binding.showWebButton.setOnClickListener {
            if (openViewID.toInt() != 0) closeView()
            openViewID = 2
            val siteURL = user.website
            if (!fragmentTypeIsActivity) {
                binding.centralLine.setGuidelinePercent(0.5F)
            }
            binding.webSiteViewElement.visibility = View.VISIBLE
            binding.closeMapOrWebButton.visibility = View.VISIBLE
            binding.webSiteViewElement.loadUrl(siteURL)
        }

        binding.showMapButton.setOnClickListener {
            if (openViewID.toInt() != 0) closeView()
            openViewID = 1
            if (!fragmentTypeIsActivity) {
                binding.centralLine.setGuidelinePercent(0.5F)
            }
            binding.mapViewWrapper.visibility = View.VISIBLE
            binding.closeMapOrWebButton.visibility = View.VISIBLE
        }

        binding.closeMapOrWebButton.setOnClickListener {
            closeView()
        }
    }

    private fun closeView() {
        if (!fragmentTypeIsActivity) {
            binding.centralLine.setGuidelinePercent(1F)
        }
        if (openViewID.toInt() == 1){
            binding.mapViewWrapper.visibility = View.INVISIBLE
            binding.closeMapOrWebButton.visibility = View.INVISIBLE
        } else if (openViewID.toInt() == 2){
            binding.webSiteViewElement.visibility = View.INVISIBLE
            binding.closeMapOrWebButton.visibility = View.INVISIBLE
        }
    }

    override fun onMapReady(googleMap : GoogleMap) {
        val lat = user.address.geo.lat.toDouble()
        val lng = user.address.geo.lng.toDouble()
        val adr: String = user.address.city + ", " + user.address.street + ", " + user.address.suite
        googleMap.addMarker(MarkerOptions().position(LatLng(lat, lng)).title(adr))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 10f))
    }
}