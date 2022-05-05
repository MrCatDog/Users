package com.example.users.model.database.utils

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.users.model.database.DatabaseUser

@Database(entities = [DatabaseUser::class], version = 1, exportSchema = false)
@TypeConverters(DataConverters::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
