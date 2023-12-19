package com.mycompany.testtask.database.data

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class Address(
    val street: String?,
    val suite: String?,
    val city: String?,
    @ColumnInfo(name = "zipcode") val zipCode: String?,
    @Embedded val geo: Geo
)
