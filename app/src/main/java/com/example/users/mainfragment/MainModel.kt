package com.example.users.mainfragment

import java.util.*

enum class FavFruit {
    STRAWBERRY,
    APPLE,
    BANANA
}

data class BaseUserInfo(
    val name: String,
    val email: String,
    val isActive: Boolean
)

data class FullUserInfo(
    val id: Int,
    val baseUserInfo: BaseUserInfo,
    val age: Int,
    val eyeColor: Int,
    val company: String,
    val address: String,
    val about: String,
    val favoriteFruit: FavFruit,
    val registered: Date,
    val lat: Float,
    val lon: Float,
    val friends: List<Int>
)

