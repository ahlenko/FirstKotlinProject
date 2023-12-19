package com.mycompany.testtask.threads

import com.mycompany.testtask.database.data.User
import retrofit2.http.GET


interface ApiService {
    @GET("users/")
    suspend fun getUsers(): List<User>
}