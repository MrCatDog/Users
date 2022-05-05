package com.example.users.model.database

import com.example.users.R
import com.example.users.model.domain.FullUserInfo
import com.example.users.usersfragment.model.mappers.Mapper
import javax.inject.Inject

class UserDatabaseMapper @Inject constructor() : Mapper<DatabaseUser, FullUserInfo> {
    override fun map(input: DatabaseUser): FullUserInfo =
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
            registeredDate = input.registeredDate,
            location = input.location,
            friends = input.friends
        )

    override fun unmap(input: FullUserInfo): DatabaseUser =
        DatabaseUser(
            id = input.baseUserInfo.id,
            name = input.baseUserInfo.name,
            email = input.baseUserInfo.email,
            isActive = input.baseUserInfo.isActive,
            age = input.age,
            eyeColor = when (input.eyeColor) {
                R.color.user_eye_color_brown -> "brown"
                R.color.user_eye_color_blue -> "blue"
                R.color.user_eye_color_green -> "green"
                else -> "no data"
            },
            company = input.company,
            phone = input.phone,
            address = input.address,
            about = input.about,
            favoriteFruit = when (input.favoriteFruit) {
                R.string.user_fav_fruit_apple -> "apple"
                R.string.user_fav_fruit_banana -> "banana"
                R.string.user_fav_fruit_strawberry -> "strawberry"
                else -> "unknown"
            },
            registeredDate = input.registeredDate,
            location = input.location,
            friends = input.friends
        )
}