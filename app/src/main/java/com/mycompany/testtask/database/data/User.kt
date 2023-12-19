package com.mycompany.testtask.database.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Int,
    val name: String?,
    val username: String?,
    val email: String?,
    @Embedded val address: Address,
    val phone: String?,
    val website: String?,
    @Embedded val company: Company
)
