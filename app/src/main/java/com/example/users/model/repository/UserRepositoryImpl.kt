package com.example.users.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.users.model.database.asBaseInfoList
import com.example.users.model.domain.FullUserInfo
import com.example.users.model.database.asDatabaseDTO
import com.example.users.model.database.asDomainModel
import com.example.users.model.database.utils.UserDao
import com.example.users.model.network.asDomainModel
import com.example.users.model.network.utils.ServerApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val serverApi: ServerApi,
    private val database: UserDao
) : UserRepository {

    private val _users = MutableLiveData<List<FullUserInfo>>()
    override val users: LiveData<List<FullUserInfo.BaseUserInfo>> =
        Transformations.map(_users) {
            it.asBaseInfoList() //todo: need to return only baseInfo from DB and do not translate it here
        }

    private val _error = MutableLiveData<ResultWrapper.Failure>()
    val error: LiveData<ResultWrapper.Failure>
        get() = _error

    override suspend fun getUsers() {
        //todo переосмыслить эту хрень part of business logic, should be in VM
        val answer = loadUsersFromDB()
        if (answer is ResultWrapper.Success && answer.value.isEmpty()) {
            loadUsresFromNetwork()
        } else {
            when (answer) {
                is ResultWrapper.Success -> _users.postValue(answer.value)
                is ResultWrapper.Failure -> _error.postValue()
            }
            val list = answer as ResultWrapper.Success
            _users.postValue(list.value)
        }
    }

    override suspend fun loadUsersFromDB(): ResultWrapper<List<FullUserInfo>> {
        return when (val answer = safeCall(Dispatchers.IO) { database.getAll() }) {
            is ResultWrapper.Success -> ResultWrapper.Success(answer.value.asDomainModel())
            is ResultWrapper.Failure -> ResultWrapper.Failure(answer.error)
        }
    }

    override suspend fun loadUsresFromNetwork() {
        when (val answer = safeCall(Dispatchers.IO) { serverApi.getUserList() }) {
            is ResultWrapper.Success -> {
                val users = answer.value.asDomainModel()
                saveUsersInDB(users)
                _users.postValue(users)
            }
            is ResultWrapper.Failure -> _error.postValue(answer)
        }
    }

    override suspend fun saveUsersInDB(users: List<FullUserInfo>) {
        database.apply {
            cleanTable()
            insertAll(users.asDatabaseDTO())
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