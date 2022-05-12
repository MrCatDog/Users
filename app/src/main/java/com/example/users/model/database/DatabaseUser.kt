package com.example.users.model.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.users.R
import com.example.users.model.domain.FullUserInfo

@Entity(tableName = "users")
data class DatabaseUser(
    @PrimaryKey val id: Int,
    val name: String,
    val email: String,
    val isActive: Boolean,
    val age: Int,
    val eyeColor: String,
    val company: String,
    val phone: String,
    val address: String,
    val about: String,
    val favoriteFruit: String,
    val registeredDate: String,
    @Embedded val location: FullUserInfo.Location,
    val friends: Set<Int>
)

//todo строки в переменные, ебанутый!
fun List<DatabaseUser>.asDomainModel() : List<FullUserInfo> {
    return map {
        FullUserInfo(
            baseUserInfo = FullUserInfo.BaseUserInfo(
                id = it.id,
                name = it.name,
                email = it.email,
                isActive = it.isActive
            ),
            age = it.age,
            eyeColor = when (it.eyeColor) {
                "brown" -> R.color.user_eye_color_brown
                "blue" -> R.color.user_eye_color_blue
                "green" -> R.color.user_eye_color_green
                else -> R.color.white
            },
            company = it.company,
            phone = it.phone,
            address = it.address,
            about = it.about,
            favoriteFruit = when (it.favoriteFruit) {
                "apple" -> R.string.user_fav_fruit_apple
                "banana" -> R.string.user_fav_fruit_banana
                "strawberry" -> R.string.user_fav_fruit_strawberry
                else -> R.string.user_fav_fruit_unknown
            },
            registeredDate = it.registeredDate,
            location = it.location,
            friends = it.friends
        )
    }
}

fun List<FullUserInfo>.asDatabaseDTO() : List<DatabaseUser> {
    return map {
        DatabaseUser(
            id = it.baseUserInfo.id,
            name = it.baseUserInfo.name,
            email = it.baseUserInfo.email,
            isActive = it.baseUserInfo.isActive,
            age = it.age,
            eyeColor = when (it.eyeColor) {
                R.color.user_eye_color_brown -> "brown"
                R.color.user_eye_color_blue -> "blue"
                R.color.user_eye_color_green -> "green"
                else -> "no data"
            },
            company = it.company,
            phone = it.phone,
            address = it.address,
            about = it.about,
            favoriteFruit = when (it.favoriteFruit) {
                R.string.user_fav_fruit_apple -> "apple"
                R.string.user_fav_fruit_banana -> "banana"
                R.string.user_fav_fruit_strawberry -> "strawberry"
                else -> "unknown"
            },
            registeredDate = it.registeredDate,
            location = it.location,
            friends = it.friends
        )
    }
}

fun List<FullUserInfo>.asBaseInfoList() : List<FullUserInfo.BaseUserInfo> {
    return map {
        it.baseUserInfo
    }
}