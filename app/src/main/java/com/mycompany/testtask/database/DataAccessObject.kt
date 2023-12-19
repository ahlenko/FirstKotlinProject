package com.mycompany.testtask.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mycompany.testtask.database.data.User

@Dao
public abstract interface DataAccessObject {
    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getCurrentUsers(userId: Int): User

    @Query("SELECT count(*) FROM users")
    fun getUsersCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User):Void
}