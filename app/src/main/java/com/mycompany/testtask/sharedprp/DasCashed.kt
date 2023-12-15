package com.mycompany.testtask.sharedprp

import android.content.Context
import android.content.SharedPreferences

class DasCashed (context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPreference", Context.MODE_PRIVATE)

        private val KEY_BOOLEAN_VALUE = "isCashedVal"

        fun saveCashedState(value: Boolean) {
            val editor = sharedPreferences.edit()
            editor.putBoolean(KEY_BOOLEAN_VALUE, value)
            editor.apply()
        }

        fun isCashed(): Boolean {
            return sharedPreferences.getBoolean(KEY_BOOLEAN_VALUE, false)
        }
}