package com.example.users.utils.cachedatabase

import androidx.room.TypeConverter
import com.example.users.mainfragment.BaseUserInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverters {

    @TypeConverter
    fun fromBaseUserInfo(value: BaseUserInfo): String {
        val gson = Gson()
        val type = object : TypeToken<BaseUserInfo>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toBaseUserInfo(value: String): BaseUserInfo {
        val gson = Gson()
        val type = object : TypeToken<BaseUserInfo>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromFriendsIdSet(value: Set<Int>): String {
        val gson = Gson()
        val type = object : TypeToken<Set<Int>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toFriendsIdSet(value: String): Set<Int> {
        val gson = Gson()
        val type = object : TypeToken<Set<Int>>() {}.type
        return gson.fromJson(value, type)
    }
}