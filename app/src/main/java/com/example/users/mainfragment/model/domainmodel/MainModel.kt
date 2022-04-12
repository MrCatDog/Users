package com.example.users.mainfragment.model.domainmodel

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

class MainModel {
    var items = ArrayList<FullUserInfo>()
}

@Entity(tableName = "users")
data class FullUserInfo(
    @PrimaryKey val guid: String,
    @Embedded val baseUserInfo: BaseUserInfo,
    val age: Int,
    val eyeColor: Int,
    val company: String,
    val phone: String,
    val address: String,
    val about: String,
    val favoriteFruit: Int,
    val registeredDate: String,
    @Embedded val location: Location,
    val friends: Set<Int>
) {
    data class BaseUserInfo(
        val id: Int,
        val name: String,
        val email: String,
        val isActive: Boolean
    )
    data class Location(
        val lat: Float,
        val lon: Float
    )
}