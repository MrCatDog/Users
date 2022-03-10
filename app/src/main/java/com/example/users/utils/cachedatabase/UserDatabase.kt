package com.example.users.utils.cachedatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.users.mainfragment.FullUserInfo

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [FullUserInfo::class], version = 1, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).allowMainThreadQueries().build() //todo ну чо, корутины? https://stackoverflow.com/questions/44167111/android-room-simple-select-query-cannot-access-database-on-the-main-thread
                INSTANCE = instance
                instance
            }
        }
    }
}
