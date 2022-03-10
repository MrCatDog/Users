package com.example.users.mainfragment

import androidx.room.Entity
import androidx.room.PrimaryKey

data class BaseUserInfo(
    val id: Int,
    val name: String,
    val email: String,
    val isActive: Boolean
)

@Entity(tableName = "users")
data class FullUserInfo(
    @PrimaryKey val guid: String,
    val baseUserInfo: BaseUserInfo,
    val age: Int,
    val eyeColor: Int,
    val company: String,
    val phone: String,
    val address: String,
    val about: String,
    val favoriteFruit: Int,
    val registeredDate: String,
    val lat: Float,
    val lon: Float,
    val friends: Set<Int>
)

class MainModel {
    var items = ArrayList<FullUserInfo>()
}

