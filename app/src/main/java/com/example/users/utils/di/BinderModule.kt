package com.example.users.utils.di

import com.example.users.mainfragment.model.domainmodel.FullUserInfo
import com.example.users.mainfragment.model.dto.NetworkUser
import com.example.users.mainfragment.model.mappers.Mapper
import com.example.users.mainfragment.model.mappers.UserNetworkMapper
import com.example.users.mainfragment.model.repository.UserRepository
import com.example.users.mainfragment.model.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class BinderModule {
    @Binds
    abstract fun bindUserMainRepository(userRepository: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindNetworkMapper(userNetworkMapper: UserNetworkMapper): Mapper<NetworkUser, FullUserInfo>
}