package com.example.users.utils.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.users.mainfragment.model.domainmodel.FullUserInfo

@Database(entities = [FullUserInfo::class], version = 1, exportSchema = false)
@TypeConverters(DataConverters::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
