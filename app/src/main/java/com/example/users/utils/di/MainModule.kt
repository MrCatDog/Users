package com.example.users.utils.di

import com.example.users.mainfragment.MainViewModel
import com.example.users.mainfragment.model.domainmodel.FullUserInfo
import com.example.users.mainfragment.model.dto.NetworkUser
import com.example.users.mainfragment.model.mappers.ListMapper
import com.example.users.mainfragment.model.mappers.ListMapperImpl
import com.example.users.mainfragment.model.mappers.UserNetworkMapper
import com.example.users.mainfragment.model.repository.UserMainRepository
import com.example.users.mainfragment.model.repository.UserRepository
import com.example.users.utils.cachedatabase.UserDao
import com.example.users.utils.network.ServerApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainModule {

    @Provides
    fun provideMainViewModel(repository:UserMainRepository) : MainViewModel =
        MainViewModel(repository)

    @Singleton
    @Provides
    fun provideUserRepository(serverApi: ServerApi,
                              cache: UserDao,
                              userNetworkMapper: ListMapper<NetworkUser, FullUserInfo>
    ) : UserMainRepository = UserRepository(serverApi, cache, userNetworkMapper)

    @Provides
    fun provideUserNetworkMapper() : ListMapper<NetworkUser, FullUserInfo> = ListMapperImpl(UserNetworkMapper())

}