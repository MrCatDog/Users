package com.example.users.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.users.model.domain.utils.ListMapper
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
    private val database: UserDao,
    private val userNetworkMapper: ListMapper<NetworkUser, FullUserInfo>,
    private val userDatabaseMapper: ListMapper<DatabaseUser, FullUserInfo>
) : UserRepository {

    private val _users = MutableLiveData<ResultWrapper<List<FullUserInfo>>>()
    override val users: LiveData<ResultWrapper<List<FullUserInfo.BaseUserInfo>>> = Transformations.map(_users) {
        return@map when (it) {
            it is ResultWrapper.Success -> it
            it is ResultWrapper.Failure -> it
        }
    }

    override suspend fun getUsers() {
        //todo переосмыслить эту хрень
        val answer = loadUsers()
        if (answer is ResultWrapper.Success && answer.value.isEmpty()) {
            updateUsers()
        } else {
            _users.postValue(answer)
        }
    }

    override suspend fun loadUsers(): ResultWrapper<List<FullUserInfo>> {
        return when (val answer = safeCall(Dispatchers.IO) { database.getAll() }) {
            is ResultWrapper.Success -> ResultWrapper.Success(userDatabaseMapper.map(answer.value))
            is ResultWrapper.Failure -> ResultWrapper.Failure(answer.error)
        }
    }

    override suspend fun saveUsers(users: List<FullUserInfo>) {
        database.apply {
            cleanTable()
            insertAll(userDatabaseMapper.unmap(users))
        }
    }

    override suspend fun updateUsers() {
        _users.postValue(
            when (val answer = safeCall(Dispatchers.IO) { serverApi.getUserList() }) {
                is ResultWrapper.Success -> run {
                    val users = userNetworkMapper.map(answer.value)
                    saveUsers(users)
                    ResultWrapper.Success(users)
                }
                is ResultWrapper.Failure -> ResultWrapper.Failure(answer.error)
            }
        )
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