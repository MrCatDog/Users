package com.example.users.model.database.utils

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.users.model.database.DatabaseUser
import com.example.users.model.domain.FullUserInfo

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): List<DatabaseUser>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<DatabaseUser>)

    @Query("DELETE FROM users")
    fun cleanTable()

    @Query("SELECT id, name, email, isActive FROM users")
    fun getAllBaseInfo() : List<FullUserInfo.BaseUserInfo>

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserFullInfo(userId : Int)

    @Query("SELECT * FROM users WHERE id IN (:usersId)")
    fun getUsersById(usersId: List<Int>)

}