package com.mycompany.testtask.database.data

import androidx.room.ColumnInfo

data class Company(
    @ColumnInfo(name = "company_name") val name: String?,
    @ColumnInfo(name = "catch_phrase") val catchPhrase: String?,
    val bs: String?
)
