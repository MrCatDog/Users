package com.example.users.model.database.utils

import androidx.room.*
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

    //todo FullUserInfo.BaseUserInfo не DTO. И не похеру ли?
    @Query("SELECT id, name, email, isActive FROM users")
    fun getAllBaseInfo() : List<FullUserInfo.BaseUserInfo>

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserFullInfo(userId : Int) : DatabaseUser

    @Query("SELECT id, name, email, isActive FROM users WHERE id IN (:usersId)")
    fun getUsersBaseInfoById(usersId: List<Int>) : List<FullUserInfo.BaseUserInfo>

}