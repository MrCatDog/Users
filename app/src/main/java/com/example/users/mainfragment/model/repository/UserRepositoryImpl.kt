package com.example.users.mainfragment.model.repository

import com.example.users.mainfragment.model.mappers.ListMapper
import com.example.users.mainfragment.model.domainmodel.FullUserInfo
import com.example.users.mainfragment.model.dto.NetworkUser
import com.example.users.utils.database.UserDao
import com.example.users.utils.network.ServerApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val cache: UserDao,
    private val userNetworkMapper: ListMapper<NetworkUser, FullUserInfo>
) : UserRepository {

    override suspend fun getUsers(): ResultWrapper<List<FullUserInfo>> {
        //todo переосмыслить эту хрень
        val users = loadUsers()
        return if (users is ResultWrapper.Success && users.value.isEmpty()) {
            updateUsers()
        } else {
            users
        }
    }

    override suspend fun loadUsers(): ResultWrapper<List<FullUserInfo>> {
        return when (val answer = safeCall(Dispatchers.IO) { cache.getAll() }) {
            is ResultWrapper.Success -> ResultWrapper.Success(answer.value)
            is ResultWrapper.Failure -> ResultWrapper.Failure(answer.error)
        }
    }

    override suspend fun saveUsers(users: List<FullUserInfo>) {
        cache.cleanTable()
        cache.insertAll(users)
    }

    override suspend fun updateUsers(): ResultWrapper<List<FullUserInfo>> {
        return when (val answer = safeCall(Dispatchers.IO) { serverApi.getUserList() }) {
            is ResultWrapper.Success -> run {
                val users = userNetworkMapper.map(answer.value)
                saveUsers(users)
                ResultWrapper.Success(users)
            }
            is ResultWrapper.Failure -> ResultWrapper.Failure(answer.error)
        }
    }
}

suspend fun <T> safeCall(
    dispatcher: CoroutineDispatcher,
    Call: suspend () -> T
): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(Call.invoke())
        } catch (throwable: Throwable) {
            ResultWrapper.Failure(throwable)
        }
    }
}

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Failure(val error: Throwable? = null) : ResultWrapper<Nothing>()
}