package com.mycompany.testtask.database.data

import androidx.room.Embedded
import androidx.room.PrimaryKey
import com.mycompany.testtask.database.data.Address
import com.mycompany.testtask.database.data.Company

data class Geo(
    val lat: String?,
    val lng: String?
)