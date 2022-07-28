package com.example.users.model.database.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverters {

    private val gson = Gson()

    @TypeConverter
    fun fromFriendsIdSet(value: Set<Int>): String {
        val type = object : TypeToken<Set<Int>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toFriendsIdSet(value: String): Set<Int> {
        val type = object : TypeToken<Set<Int>>() {}.type
        return gson.fromJson(value, type)
    }
}