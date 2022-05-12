package com.example.users.utils.di

import com.example.users.model.domain.FullUserInfo
import com.example.users.model.database.DatabaseUser
import com.example.users.model.network.NetworkUser
import com.example.users.model.domain.utils.Mapper
import com.example.users.model.database.UserDatabaseMapper
import com.example.users.model.network.UserNetworkMapper
import com.example.users.model.repository.UserRepository
import com.example.users.model.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class BinderModule {
    @Binds
    abstract fun bindUserMainRepository(userRepository: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindNetworkMapper(userNetworkMapper: UserNetworkMapper): Mapper<NetworkUser, FullUserInfo>

    @Binds
    abstract fun bindDatabaseMapper(userDatabaseMapper: UserDatabaseMapper): Mapper<DatabaseUser, FullUserInfo>
}