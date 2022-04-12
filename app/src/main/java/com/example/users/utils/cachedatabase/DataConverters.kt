package com.example.users.utils.cachedatabase

import androidx.room.TypeConverter
import com.example.users.mainfragment.model.domainmodel.FullUserInfo.BaseUserInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverters {

    private val gson = Gson() //todo DI же надо

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