package com.example.users.model.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.users.R
import com.example.users.model.domain.FullUserInfo
import com.example.users.model.database.DatabaseUser.Companion.COLORS
import com.example.users.model.database.DatabaseUser.Companion.FRUITS

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
) {
    fun asDomainModel(): FullUserInfo =
        FullUserInfo(
            baseUserInfo = FullUserInfo.BaseUserInfo(
                id = this.id,
                name = this.name,
                email = this.email,
                isActive = this.isActive
            ),
            age = this.age,
            eyeColor = COLORS.getOrDefault(this.eyeColor, R.color.white),
            company = this.company,
            phone = this.phone,
            address = this.address,
            about = this.about,
            favoriteFruit = FRUITS.getOrDefault(this.favoriteFruit, R.string.user_fav_fruit_unknown),
            registeredDate = this.registeredDate,
            location = this.location,
            friends = this.friends
        )

    companion object {
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

//todo строки в переменные, ебанутый!
fun List<DatabaseUser>.asDomainModel(): List<FullUserInfo> {
    return map {
        it.asDomainModel()
    }
}

fun List<FullUserInfo>.asDatabaseDTO(): List<DatabaseUser> {
    return map {
        DatabaseUser(
            id = it.baseUserInfo.id,
            name = it.baseUserInfo.name,
            email = it.baseUserInfo.email,
            isActive = it.baseUserInfo.isActive,
            age = it.age,
            eyeColor = COLORS.getOrDefault(it.eyeColor, "no data") //todo обратное преобразование мапы?
            when (it.eyeColor) {
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

fun List<FullUserInfo>.asBaseInfoList(): List<FullUserInfo.BaseUserInfo> {
    return map {
        it.baseUserInfo
    }
}