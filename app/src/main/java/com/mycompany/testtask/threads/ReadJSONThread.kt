package com.mycompany.testtask.threads

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mycompany.testtask.R
import com.mycompany.testtask.SplashScreenActivity
import com.mycompany.testtask.data.Adress
import com.mycompany.testtask.data.Company
import com.mycompany.testtask.data.Geo
import com.mycompany.testtask.data.User
import com.mycompany.testtask.usersdata.UserList
import com.squareup.picasso.Picasso
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

class ReadJSONThread(link : String, context: Context) : Thread() {
    val KEY_ID = "id"
    val KEY_NAME = "name"
    val KEY_USERNAME = "username"
    val KEY_EMAIL = "email"
    val KEY_ADDRESS = "address"
    val KEY_STREET = "street"
    val KEY_SUITE = "suite"
    val KEY_CITY = "city"
    val KEY_ZIPCODE = "zipcode"
    val KEY_GEO = "geo"
    val KEY_LAT = "lat"
    val KEY_LNG = "lng"
    val KEY_PHONE = "phone"
    val KEY_WEBSITE = "website"
    val KEY_COMPANY = "company"
    val KEY_COMPANY_NAME = "name"
    val KEY_CATCH_PHRASE = "catchPhrase"
    val KEY_BS = "bs"

    final val linkFinal = link
    final val contextFinal = context

    override fun run() {
        val listOfUser = UserList(contextFinal)
        val url = URL(linkFinal)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = StringBuilder()
        var line: String?

        while (reader.readLine().also { line = it } != null) response.append(line)

        reader.close()
        connection.disconnect()
        val jsonArray = JsonParser.parseString(response.toString()).asJsonArray
        for (i in 0 until jsonArray.size()){
            val userObject = jsonArray[i].asJsonObject

            val companyObject: JsonObject = userObject.getAsJsonObject(KEY_COMPANY)
            val addressObject: JsonObject = userObject.getAsJsonObject(KEY_ADDRESS)
            val geoObject: JsonObject = addressObject.getAsJsonObject(KEY_GEO)

            val userTemp = User(
                id = userObject.get(KEY_ID).getAsInt(),
                name = userObject.get(KEY_NAME).getAsString(),
                username = userObject.get(KEY_USERNAME).getAsString(),
                email = userObject.get(KEY_EMAIL).getAsString(),
                address = Adress(
                    street = addressObject.get(KEY_STREET).getAsString(),
                    suite = addressObject.get(KEY_SUITE).getAsString(),
                    city = addressObject.get(KEY_CITY).getAsString(),
                    zipcode = addressObject.get(KEY_ZIPCODE).getAsString(),
                    geo = Geo(
                        lat = geoObject.get(KEY_LAT).getAsString(),
                        lng = geoObject.get(KEY_LNG).getAsString()
                    )
                ), phone = userObject.get(KEY_PHONE).getAsString(),
                website = userObject.get(KEY_WEBSITE).getAsString(),
                company = Company(
                    name = companyObject.get(KEY_COMPANY_NAME).getAsString(),
                    catchPhrase = companyObject.get(KEY_CATCH_PHRASE).getAsString(),
                    bs = companyObject.get(KEY_BS).getAsString()
                )
            )
            listOfUser.addUser(userTemp)
        }
    }
}