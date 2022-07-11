package com.example.users.model.network

import com.example.users.R
import com.example.users.model.domain.FullUserInfo
import com.example.users.model.network.NetworkUser.Companion.COLORS
import com.example.users.model.network.NetworkUser.Companion.FORMAT_DATE_PATTERN
import com.example.users.model.network.NetworkUser.Companion.FRUITS
import com.example.users.model.network.NetworkUser.Companion.PARSE_DATE_PATTERN
import java.text.SimpleDateFormat
import java.util.*

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

    companion object {
        const val PARSE_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss Z"
        const val FORMAT_DATE_PATTERN = "HH:mm dd.MM.yy"
        val COLORS = mapOf(
            "blue" to R.color.user_eye_color_blue,
            "brown" to R.color.user_eye_color_brown,
            "green" to R.color.user_eye_color_green
        )
        val FRUITS = mapOf(
            "apple" to R.string.user_fav_fruit_apple,
            "banana" to R.string.user_fav_fruit_banana,
            "strawberry" to R.string.user_fav_fruit_strawberry
        )
    }
}

fun List<NetworkUser>.asDomainModel(): List<FullUserInfo> {
    return map {
        FullUserInfo(
            baseUserInfo = FullUserInfo.BaseUserInfo(
                id = it.id,
                name = it.name,
                email = it.email,
                isActive = it.isActive
            ),
            age = it.age,
            eyeColor = COLORS.getOrDefault(it.eyeColor, R.color.white),
            company = it.company,
            phone = it.phone,
            address = it.address,
            about = it.about.trim(),
            favoriteFruit = FRUITS.getOrDefault(it.favoriteFruit, R.string.user_fav_fruit_unknown),
            registeredDate = reformatDate(it.registered),
            location = FullUserInfo.Location(it.latitude, it.longitude),
            friends = it.friends.map { friend -> friend.id }.toSet() //todo как это перевести в домейн? Ведь сеть вернёт список чисел, а не пользователей.
        )
    }
}

private fun reformatDate(textDate: String) = formatDate(parseDate(textDate))

private fun parseDate(textDate: String) =
    SimpleDateFormat(PARSE_DATE_PATTERN, Locale.US).parse(textDate) as Date

private fun formatDate(date: Date) =
    SimpleDateFormat(FORMAT_DATE_PATTERN, Locale.getDefault()).format(date).toString()