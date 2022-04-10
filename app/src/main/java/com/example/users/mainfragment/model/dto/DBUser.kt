package com.example.users.mainfragment.model.dto

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.users.mainfragment.model.domainmodel.FullUserInfo

//todo ты уж определись, или mapper или typeConverter
@Entity(tableName = "users")
data class DBUser(
    @PrimaryKey val id: Int,
    val name: String,
    val email: String,
    val isActive: Boolean,
    val age: Int,
    val eyeColor: String,
    val company: String,
    val phone: String,
    val address: String,
    val about: String,
    val favoriteFruit: String,
    val registeredDate: String,
    @Embedded val location: FullUserInfo.Location,
    val friends: Set<Int>
)