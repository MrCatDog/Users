package com.example.users.model.repository

import com.example.users.usersfragment.model.mappers.ListMapper
import com.example.users.model.domain.FullUserInfo
import com.example.users.model.database.DatabaseUser
import com.example.users.model.network.NetworkUser
import com.example.users.model.database.utils.UserDao
import com.example.users.model.network.utils.ServerApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val cache: UserDao,
    private val userNetworkMapper: ListMapper<NetworkUser, FullUserInfo>,
    private val userDatabaseMapper: ListMapper<DatabaseUser, FullUserInfo>
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
            is ResultWrapper.Success -> ResultWrapper.Success(userDatabaseMapper.map(answer.value))
            is ResultWrapper.Failure -> ResultWrapper.Failure(answer.error)
        }
    }

    override suspend fun saveUsers(users: List<FullUserInfo>) {
        cache.cleanTable()
        cache.insertAll(userDatabaseMapper.unmap(users))
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