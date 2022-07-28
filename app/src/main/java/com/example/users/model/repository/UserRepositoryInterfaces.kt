package com.example.users.model.repository

import com.example.users.model.domain.FullUserInfo

interface UserRepository : UserDBRepository, UserNetworkRepository

interface UserNetworkRepository {
    suspend fun loadUsersFromNetwork() : ResultWrapper<List<FullUserInfo>>
}

interface UserDBRepository {
    suspend fun loadBaseUsersInfoFromDB(idList: List<Int> = emptyList()) : ResultWrapper<List<FullUserInfo.BaseUserInfo>>
    suspend fun saveUsersInDB(users: List<FullUserInfo>)
    suspend fun loadUserDetails(id: Int) : ResultWrapper<FullUserInfo>
}

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Failure(val error: ErrorEntity) : ResultWrapper<Nothing>()
}

sealed class ErrorEntity {
    sealed class ApiError : ErrorEntity() {
        object Network : ApiError()
        object NotFound : ApiError()
        object AccessDenied : ApiError()
        object ServiceUnavailable : ApiError()
    }

    sealed class DBError : ErrorEntity() {
        object NoPermission : DBError()
        object Common: DBError()
    }

    object UnknownError : ErrorEntity()
}