package com.example.users.mainfragment.model.repository

import com.example.users.mainfragment.model.mappers.ListMapper
import com.example.users.mainfragment.model.domainmodel.FullUserInfo
import com.example.users.mainfragment.model.dto.DBUser
import com.example.users.mainfragment.model.dto.NetworkUser
import com.example.users.utils.cachedatabase.UserDao
import com.example.users.utils.network.ServerApi
import javax.inject.Inject

class UserRepository(
   private val serverApi: ServerApi,
   private val cache: UserDao,
   private val userNetworkMapper: ListMapper<NetworkUser, FullUserInfo>,
   private val userDBMapper: ListMapper<DBUser, FullUserInfo>
) : UserDBRepository, UserNetworkRepository {

    override fun loadUsersFromDB(): List<FullUserInfo> {
        TODO("Not yet implemented")
    }

    override fun updateUsersFromNetwork(): List<FullUserInfo> {
        TODO("Not yet implemented")
    }
}

sealed class Result<T> {

    data class Success<T>(val value: T) : Result<T>()

    data class Failure<T>(val throwable: Throwable) : Result<T>()

}