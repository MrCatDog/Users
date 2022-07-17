package com.example.users.model.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.users.R
import com.example.users.model.domain.FullUserInfo
import com.example.users.model.database.DBUserWithFriends.Companion.COLORS
import com.example.users.model.database.DBUserWithFriends.Companion.DEFAULT_RECORD_FOR_UNKNOWN
import com.example.users.model.database.DBUserWithFriends.Companion.FRUITS

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
    //todo: relation annotation
    val friends: Set<Int>
) {
//    fun asDomainModel(): FullUserInfo =
//        FullUserInfo(
//            baseUserInfo = FullUserInfo.BaseUserInfo(
//                id = this.id,
//                name = this.name,
//                email = this.email,
//                isActive = this.isActive
//            ),
//            age = this.age,
//            eyeColor = COLORS.getOrDefault(this.eyeColor, R.color.white),
//            company = this.company,
//            phone = this.phone,
//            address = this.address,
//            about = this.about,
//            favoriteFruit = FRUITS.getOrDefault(
//                this.favoriteFruit,
//                R.string.user_fav_fruit_unknown
//            ),
//            registeredDate = this.registeredDate,
//            location = this.location,
//            friends = this.friends.map {
//                FullUserInfo.BaseUserInfo(
//                    id = this.id,
//                    name = it.name,
//                    email = it.email,
//                    isActive = it.isActive
//                )
//            }.toSet()
//        )
//
//    companion object {
//        val COLORS = mapOf(
//            "blue" to R.color.user_eye_color_blue,
//            "brown" to R.color.user_eye_color_brown,
//            "green" to R.color.user_eye_color_green
//        )
//        val FRUITS = mapOf(
//            "apple" to R.string.user_fav_fruit_apple,
//            "banana" to R.string.user_fav_fruit_banana,
//            "strawberry" to R.string.user_fav_fruit_strawberry
//        )
//        const val DEFAULT_RECORD_FOR_UNKNOWN = "no data"
//    }
}

fun List<FullUserInfo>.asDatabaseDTO(): List<DatabaseUser> {
    return map {
        DatabaseUser(
            id = it.baseUserInfo.id,
            name = it.baseUserInfo.name,
            email = it.baseUserInfo.email,
            isActive = it.baseUserInfo.isActive,
            age = it.age,
            eyeColor = COLORS.entries.find { mapEnt -> mapEnt.value == it.eyeColor }?.key
                ?: DEFAULT_RECORD_FOR_UNKNOWN,
            company = it.company,
            phone = it.phone,
            address = it.address,
            about = it.about,
            favoriteFruit = FRUITS.entries.find { mapEnt -> mapEnt.value == it.favoriteFruit }?.key
                ?: DEFAULT_RECORD_FOR_UNKNOWN,
            registeredDate = it.registeredDate,
            location = it.location,
            friends = it.friends.map { friend -> friend.id }.toSet()
        )
    }
}

data class DBUserWithFriends(
    @Embedded
    val user: DatabaseUser,
    @Relation(
        entity = DatabaseUser::class,
        parentColumn = "friends",
        entityColumn = "id",
        projection = ["id", "name", "email", "isActive"]
    )
    val friends: Set<FullUserInfo.BaseUserInfo>
) {
    fun asDomainUser() : FullUserInfo = FullUserInfo(
        baseUserInfo = FullUserInfo.BaseUserInfo(
            user.id,
            user.name,
            user.email,
            user.isActive
        ),
        age = user.age,
        eyeColor = COLORS.getOrDefault(user.eyeColor, R.color.white),
        company = user.company,
        phone = user.phone,
        address = user.address,
        about = user.about,
        favoriteFruit = FRUITS.getOrDefault(
            user.favoriteFruit,
            R.string.user_fav_fruit_unknown
        ),
        registeredDate = user.registeredDate,
        location = user.location,
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
        const val DEFAULT_RECORD_FOR_UNKNOWN = "no data"
    }
}