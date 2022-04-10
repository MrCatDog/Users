package com.example.users.mainfragment.model.mappers

import com.example.users.R
import com.example.users.mainfragment.model.domainmodel.FullUserInfo
import com.example.users.mainfragment.model.dto.DBUser

class UserDBMapper : Mapper<DBUser, FullUserInfo> {
    override fun map(input: DBUser): FullUserInfo =
        FullUserInfo(
            guid = "",
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
            registeredDate = input.registeredDate,
            location = FullUserInfo.Location(lat = input.lat, lon = input.lon),
            friends = input.friends
        )
}