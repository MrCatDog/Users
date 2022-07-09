package com.example.users.utils.di

import android.content.Context
import androidx.room.Room
import com.example.users.model.database.utils.UserDao
import com.example.users.model.database.utils.UserDatabase
import com.example.users.model.repository.errorhandlers.DBErrorHandler
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {

    companion object {
        const val databaseName = "user_database"
    }

    @Singleton
    @Provides
    fun provideDatabase(context: Context): UserDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            UserDatabase::class.java,
            databaseName
        ).build()

    @Singleton
    @Provides
    fun providePersonDao(db: UserDatabase): UserDao {
        return db.userDao()
    }

    @Provides
    fun provideDBErrorHandler(): DBErrorHandler = DBErrorHandler()
}