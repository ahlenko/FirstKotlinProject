package com.mycompany.testtask.usersdata

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.mycompany.testtask.data.User

class UserList(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserListPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val userList: MutableList<User> by lazy {
        loadUserList()
    }

    fun addUser(user: User) {
        userList.add(user)
        saveUserList()
    }

    fun getUserListAsList(): List<User> {
        return userList.toList()
    }

    fun getCurUser(num: Int): User {
        return userList[num]
    }

    private fun saveUserList() {
        sharedPreferences.edit().apply {
            val json = gson.toJson(userList)
            putString("userList", json)
        }.apply()
    }

    public fun clearUserList() {
        sharedPreferences.edit().clear().apply()
    }

    private fun loadUserList(): MutableList<User> {
        val json = sharedPreferences.getString("userList", "")
        return try {
            if (json.isNullOrEmpty()) {
                mutableListOf()
            } else {
                gson.fromJson(json, object : TypeToken<MutableList<User>>() {}.type)
            }
        } catch (e: JsonSyntaxException) {
            mutableListOf()
        }
    }
}