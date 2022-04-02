package com.example.users.mainfragment.model.repository

import com.example.users.mainfragment.model.mappers.ListMapper
import com.example.users.mainfragment.model.domainmodel.FullUserInfo
import com.example.users.mainfragment.model.dto.DBUser
import com.example.users.mainfragment.model.dto.NetworkUser
import com.example.users.utils.cachedatabase.UserDao
import com.example.users.utils.network.ServerApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
   private val serverApi: ServerApi,
   private val cache: UserDao,
   private val userNetworkMapper: ListMapper<NetworkUser, FullUserInfo>,
   private val userDBMapper: ListMapper<DBUser, FullUserInfo>
) : UserDBRepository, UserNetworkRepository {

    override fun loadUsersFromDB(): List<FullUserInfo> {
        TODO("Not yet implemented")
    }

    override fun updateUsersFromNetwork(): ResultWrapper<List<FullUserInfo>> {
        return safeApiCall(Dispatchers.IO) { serverApi.getUserList() }
    }
}

suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            ResultWrapper.Failure(throwable)
        }
    }
}

// wrapper
sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T): ResultWrapper<T>()
    data class Failure(val error: Throwable? = null): ResultWrapper<Nothing>()
}