package com.mycompany.testtask.sharedprp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.mycompany.testtask.data.User

class UserList(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PRF_NAME, Context.MODE_PRIVATE)

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

    fun clearUserList() {
        sharedPreferences.edit().clear().apply()
    }

    private fun saveUserList() {
        sharedPreferences.edit().apply {
            val json = gson.toJson(userList)
            putString(PRF_NAME, json)
        }.apply()
    }

    private fun loadUserList(): MutableList<User> {
        val json = sharedPreferences.getString(PRF_NAME, "")
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

    companion object{
        const val SHARED_PRF_NAME : String = "UserListPrefs"
        const val PRF_NAME : String = "userList"
    }
}