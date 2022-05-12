package com.example.users.utils.di

import com.example.users.model.repository.UserRepository
import com.example.users.model.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class BinderModule {
    @Binds
    abstract fun bindUserMainRepository(userRepository: UserRepositoryImpl): UserRepository
}