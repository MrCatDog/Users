package com.example.users.model.repository

import com.example.users.model.domain.FullUserInfo
import com.example.users.model.database.asDatabaseDTO
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

    override suspend fun loadBaseUsersInfoFromDB(idList: List<Int>) : ResultWrapper<List<FullUserInfo.BaseUserInfo>> {
        return safeCall(Dispatchers.IO) {
            if (idList.isEmpty()) {
                database.getAllBaseInfo()
            } else {
                database.getUsersBaseInfoById(idList)
            }
        }
    }

    override suspend fun loadUserDetails(id: Int) : ResultWrapper<FullUserInfo> {
        return safeCall(Dispatchers.IO) { database.getUserFullInfo(id).asDomainModel() }
    }

    override suspend fun loadUsersFromNetwork() : ResultWrapper<List<FullUserInfo>> {
        return safeCall(Dispatchers.IO) { serverApi.getUserList().asDomainModel() }
//        when (val answer = safeCall(Dispatchers.IO) { serverApi.getUserList() }) {
//            is ResultWrapper.Success -> saveUsersInDB(answer.value.asDomainModel())
//            //todo кусок бизнеса. Просто грузим, отвечаем удалось или нет, если удалось,
//            // то домейн решает что делать, например забрать новые значения с базы данных
//            // подумать что отсюда вообще возвращать, здесь ведь очень много ошибок
//            // https://proandroiddev.com/android-error-handling-in-clean-architecture-844a7fc0dc03
//            is ResultWrapper.Failure -> _error.postValue(answer)
//        }
    }

    override suspend fun saveUsersInDB(users: List<FullUserInfo>) {
        database.apply {
            cleanTable()
            insertAll(users.asDatabaseDTO())
        }
    }

    private suspend fun <T> safeCall(
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
}

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Failure(val error: Throwable? = null) : ResultWrapper<Nothing>()
}