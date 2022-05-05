package com.example.users.model.domain

data class FullUserInfo(
    val baseUserInfo: BaseUserInfo,
    val age: Int,
    val eyeColor: Int,
    val company: String,
    val phone: String,
    val address: String,
    val about: String,
    val favoriteFruit: Int,
    val registeredDate: String,
    val location: Location,
    val friends: Set<Int>
) {
    data class BaseUserInfo(
        val id: Int,
        val name: String,
        val email: String,
        val isActive: Boolean
    )

    data class Location(val latitude: Float, val longitude: Float)
}