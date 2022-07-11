package com.example.users.model.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.users.R
import com.example.users.model.domain.FullUserInfo
import com.example.users.model.database.DatabaseUser.Companion.COLORS
import com.example.users.model.database.DatabaseUser.Companion.DEFAULT_RECORD_FOR_UNKNOWN
import com.example.users.model.database.DatabaseUser.Companion.FRUITS

@Entity(tableName = "users")
data class DatabaseUser(
    @PrimaryKey val id: Int,
    @Embedded val baseUserInfo: BaseUserInfo,
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
    @Relation(parentColumn = "id", entityColumn = "id")
    val friends: Set<BaseUserInfo>
) {
    data class BaseUserInfo(val name: String, val email: String, val isActive: Boolean)

    fun asDomainModel(): FullUserInfo =
        FullUserInfo(
            baseUserInfo = FullUserInfo.BaseUserInfo(
                id = this.id,
                name = this.baseUserInfo.name,
                email = this.baseUserInfo.email,
                isActive = this.baseUserInfo.isActive
            ),
            age = this.age,
            eyeColor = COLORS.getOrDefault(this.eyeColor, R.color.white),
            company = this.company,
            phone = this.phone,
            address = this.address,
            about = this.about,
            favoriteFruit = FRUITS.getOrDefault(
                this.favoriteFruit,
                R.string.user_fav_fruit_unknown
            ),
            registeredDate = this.registeredDate,
            location = this.location,
            friends = this.friends.map {
                FullUserInfo.BaseUserInfo(
                    id = this.id,
                    name = it.name,
                    email = it.email,
                    isActive = it.isActive
                )
            }.toSet()
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

fun List<FullUserInfo>.asDatabaseDTO(): List<DatabaseUser> {
    return map {
        DatabaseUser(
            id = it.baseUserInfo.id,
            baseUserInfo = DatabaseUser.BaseUserInfo(
                name = it.baseUserInfo.name,
                email = it.baseUserInfo.email,
                isActive = it.baseUserInfo.isActive
            ),
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
            friends = it.friends.map { domainBase ->
                DatabaseUser.BaseUserInfo(
                    name = domainBase.name,
                    email = domainBase.email,
                    isActive = domainBase.isActive
                )
            }.toSet()
        )
    }
}