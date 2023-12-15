package com.mycompany.testtask.data

import android.graphics.Bitmap

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val address: Adress,
    val phone: String,
    val website: String,
    val company: Company
)
