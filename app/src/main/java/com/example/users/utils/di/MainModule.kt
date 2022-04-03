package com.example.users.utils.di

import com.example.users.mainfragment.model.domainmodel.FullUserInfo
import com.example.users.mainfragment.model.dto.DBUser
import com.example.users.mainfragment.model.dto.NetworkUser
import com.example.users.mainfragment.model.mappers.ListMapper
import com.example.users.mainfragment.model.mappers.ListMapperImpl
import com.example.users.mainfragment.model.mappers.UserDBMapper
import com.example.users.mainfragment.model.mappers.UserNetworkMapper
import com.example.users.mainfragment.model.repository.UserRepository
import com.example.users.utils.cachedatabase.UserDao
import com.example.users.utils.network.ServerApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainModule {

    @Singleton
    @Provides
    fun provideUserRepository(serverApi: ServerApi,
                              cache: UserDao,
                              userNetworkMapper: ListMapper<NetworkUser, FullUserInfo>,
                              userDBMapper: ListMapper<DBUser, FullUserInfo>
    ) : UserRepository = UserRepository(serverApi, cache, userNetworkMapper, userDBMapper)

    @Provides
    fun provideUserNetworkMapper() : ListMapper<NetworkUser, FullUserInfo> = ListMapperImpl(UserNetworkMapper())

    @Provides
    fun provideUserDBMapper() : ListMapper<DBUser, FullUserInfo> = ListMapperImpl(UserDBMapper())
}