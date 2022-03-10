package com.example.users.utils.cachedatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.users.mainfragment.FullUserInfo

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): List<FullUserInfo>

    @Insert
    fun insertAll(users: List<FullUserInfo>)

    @Query("DELETE FROM users")
    fun cleanTable()

}