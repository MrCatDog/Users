package com.example.users.model.network

import com.example.users.R
import com.example.users.model.domain.FullUserInfo
import com.example.users.model.domain.utils.Mapper
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class UserNetworkMapper @Inject constructor() : Mapper<NetworkUser, FullUserInfo> {

    companion object {
        const val PARSE_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss Z"
        const val FORMAT_DATE_PATTERN = "HH:mm dd.MM.yy"
    }

    override fun map(input: NetworkUser): FullUserInfo =
        FullUserInfo(
            baseUserInfo = FullUserInfo.BaseUserInfo(
                id = input.id,
                name = input.name,
                email = input.email,
                isActive = input.isActive
            ),
            age = input.age,
            eyeColor = when (input.eyeColor) {
                "brown" -> R.color.user_eye_color_brown
                "blue" -> R.color.user_eye_color_blue
                "green" -> R.color.user_eye_color_green
                else -> R.color.white
            },
            company = input.company,
            phone = input.phone,
            address = input.address,
            about = input.about,
            favoriteFruit = when (input.favoriteFruit) {
                "apple" -> R.string.user_fav_fruit_apple
                "banana" -> R.string.user_fav_fruit_banana
                "strawberry" -> R.string.user_fav_fruit_strawberry
                else -> R.string.user_fav_fruit_unknown
            },
            registeredDate = reformatDate(input.registered),
            location = FullUserInfo.Location(input.latitude, input.longitude),
            friends = input.friends.map { it.id }.toSet()
        )

    private fun reformatDate(textDate: String) = formatDate(parseDate(textDate))

    private fun parseDate(textDate: String) =
        SimpleDateFormat(PARSE_DATE_PATTERN, Locale.US).parse(textDate) as Date

    private fun formatDate(date: Date) =
        SimpleDateFormat(FORMAT_DATE_PATTERN, Locale.getDefault()).format(date).toString()

    override fun unmap(input: FullUserInfo): NetworkUser {
        TODO("Not yet implemented and still not need")
    }
}