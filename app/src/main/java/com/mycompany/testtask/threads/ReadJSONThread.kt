package com.mycompany.testtask.threads

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.mycompany.testtask.data.User
import com.mycompany.testtask.sharedprp.UserList
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ReadJSONThread(link : String, context: Context) : Thread() {
    private val linkFinal = link
    private val contextFinal = context

    override fun run() {
        val listOfUser = UserList(contextFinal)
        val url = URL(linkFinal)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = StringBuilder()
        var line: String?

        while (reader.readLine().also { line = it } != null) response.append(line)

        val gson = Gson()
        reader.close()
        connection.disconnect()

        val jsonArray = JsonParser.parseString(response.toString()).asJsonArray
        for (i in 0 until jsonArray.size()){
            val user = gson.fromJson(jsonArray[i].asJsonObject, User::class.java)
            listOfUser.addUser(user)
        }
    }
}