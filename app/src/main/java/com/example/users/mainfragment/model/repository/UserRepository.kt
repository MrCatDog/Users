package com.example.users.mainfragment.model.repository

import com.example.users.mainfragment.model.mappers.ListMapper
import com.example.users.mainfragment.model.domainmodel.FullUserInfo
import com.example.users.mainfragment.model.dto.DBUser
import com.example.users.mainfragment.model.dto.NetworkUser
import com.example.users.utils.cachedatabase.UserDao
import com.example.users.utils.network.ServerApi
import javax.inject.Inject

class UserRepository : UserDBRepository, UserNetworkRepository {

    @Inject
    lateinit var serverApi: ServerApi

    @Inject
    lateinit var cache: UserDao

    @Inject
    lateinit var userNetworkMapper: ListMapper<NetworkUser, FullUserInfo>

    @Inject
    lateinit var userDBMapper: ListMapper<DBUser, FullUserInfo>

    override fun loadUsersFromDB(): List<FullUserInfo> {
        TODO("Not yet implemented")
    }

    override fun updateUsersFromNetwork(): List<FullUserInfo> {
        TODO("Not yet implemented")
    }
}