package com.example.users.utils.di

import android.content.Context
import androidx.room.Room
import com.example.users.utils.cachedatabase.UserDao
import com.example.users.utils.cachedatabase.UserDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): UserDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            UserDatabase::class.java,
            "user_database"
        ).build()

    @Singleton
    @Provides
    fun providePersonDao(db: UserDatabase): UserDao {
        return db.userDao()
    }
}