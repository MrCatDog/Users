package com.example.users.mainfragment

import java.util.*

data class FullUserInfo(
    val id: Int,
    val baseUserInfo: BaseUserInfo,
    val age: Int,
    val eyeColor: Int,
    val company: String,
    val address: String,
    val about: String,
    val favoriteFruit: String, //todo enum?
    val registered: Date,
    val lat: Float,
    val lon: Float,
    val friends: List<Int>
)

data class BaseUserInfo(
    val name: String,
    val email: String,
    val isActive: Boolean
)