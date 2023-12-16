package com.mycompany.testtask.sharedprp

import android.content.Context
import android.content.SharedPreferences

class DasCashed (context: Context) {

    private val KEY_SHARED_PRP_NAME = "AppPreference"
    private val KEY_BOOLEAN_VALUE = "isCashedVal"

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(KEY_SHARED_PRP_NAME, Context.MODE_PRIVATE)

    fun saveCashedState(value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_BOOLEAN_VALUE, value)
        editor.apply()
    }

    fun isCashed(): Boolean {
        return sharedPreferences.getBoolean(KEY_BOOLEAN_VALUE, false)
    }
}