package com.example.users.model.network

data class NetworkUser(
    val guid: String,
    val id: Int,
    val isActive: Boolean,
    val age: Int,
    val eyeColor: String,
    val name: String,
    val company: String,
    val email: String,
    val phone: String,
    val address: String,
    val about: String,
    val registered: String,
    val latitude: Float,
    val longitude: Float,
    val friends: List<FriendsResponse>,
    val favoriteFruit: String
) {
    data class FriendsResponse(
        val id: Int
    )
}