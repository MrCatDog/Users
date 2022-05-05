package com.example.users.model.database.utils

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.users.model.database.DatabaseUser

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): List<DatabaseUser>

    @Insert
    fun insertAll(users: List<DatabaseUser>)

    @Query("DELETE FROM users")
    fun cleanTable()

}