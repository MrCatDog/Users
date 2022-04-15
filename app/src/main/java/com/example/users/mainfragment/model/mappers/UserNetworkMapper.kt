package com.example.users.mainfragment.model.mappers

import com.example.users.R
import com.example.users.mainfragment.FORMAT_DATE_PATTERN
import com.example.users.mainfragment.PARSE_DATE_PATTERN
import com.example.users.mainfragment.model.domainmodel.FullUserInfo
import com.example.users.mainfragment.model.dto.NetworkUser
import java.text.SimpleDateFormat
import java.util.*

class UserNetworkMapper : Mapper<NetworkUser, FullUserInfo> {

    override fun map(input: NetworkUser): FullUserInfo =
        FullUserInfo(
            guid = input.guid,
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
            location = FullUserInfo.Location(lat = input.latitude, lon = input.longitude),
            friends = NullableInputListMapperImpl(object :
                Mapper<NetworkUser.FriendsResponse, Int> {
                override fun map(input: NetworkUser.FriendsResponse): Int = input.id
            }).map(input.friends).toSet()
        )

    private fun reformatDate(textDate: String) = formatDate(parseDate(textDate))

    private fun parseDate(textDate: String) =
        SimpleDateFormat(PARSE_DATE_PATTERN, Locale.US).parse(textDate) as Date

    private fun formatDate(date: Date) =
        SimpleDateFormat(FORMAT_DATE_PATTERN, Locale.getDefault()).format(date).toString()
}