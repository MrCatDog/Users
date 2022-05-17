package com.example.users.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.users.model.database.asBaseInfoList
import com.example.users.model.domain.FullUserInfo
import com.example.users.model.database.asDatabaseDTO
import com.example.users.model.database.utils.UserDao
import com.example.users.model.network.asDomainModel
import com.example.users.model.network.utils.ServerApi
import com.example.users.utils.MutableLiveEvent
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

    private val _users = MutableLiveData<List<FullUserInfo.BaseUserInfo>>()
    override val users: LiveData<List<FullUserInfo.BaseUserInfo>>
        get() = _users

    private val _detailedUserInfo = MutableLiveData<FullUserInfo>()
    override val detailedUserInfo: LiveData<FullUserInfo>
        get() = _detailedUserInfo

    private val _error = MutableLiveEvent<ResultWrapper.Failure>()
    override val error: LiveData<ResultWrapper.Failure>
        get() = _error

//    override suspend fun getUsers() {
//        //todo переосмыслить эту хрень part of business logic, should be in VM
//        val answer = loadUsersFromDB()
//        if (answer is ResultWrapper.Success && answer.value.isEmpty()) {
//            loadUsresFromNetwork()
//        } else {
//            when (answer) {
//                is ResultWrapper.Success -> _users.postValue(answer.value)
//                is ResultWrapper.Failure -> _error.postValue(answer)
//            }
//            val list = answer as ResultWrapper.Success
//            _users.postValue(list.value)
//        }
//    }

    override suspend fun loadBaseUsersInfoFromDB(idList: List<Int>) {
        when (val answer: ResultWrapper<List<FullUserInfo.BaseUserInfo>> =
            if (idList.isEmpty()) {
                safeCall(Dispatchers.IO) { database.getAllBaseInfo() }
            } else {
                safeCall(Dispatchers.IO) { database.getUsersById(idList) }
            }
        ) {
            is ResultWrapper.Success -> _users.postValue(answer.value!!) //todo опять эта херня
            is ResultWrapper.Failure -> _error.postValue(answer)
        }
    }

    override suspend fun loadUserDetails(id: Int) {
        when (val answer = safeCall(Dispatchers.IO) { database.getUserFullInfo(id) }) {
            is ResultWrapper.Success -> _detailedUserInfo.postValue(answer.value.asDomainModel())
            is ResultWrapper.Failure -> _error.postValue(answer)
        }
    }

    override suspend fun loadUsersFromNetwork() {
        when (val answer = safeCall(Dispatchers.IO) { serverApi.getUserList() }) {
            is ResultWrapper.Success -> {
                val users = answer.value.asDomainModel()
                saveUsersInDB(users)
                _users.postValue(users.asBaseInfoList())
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