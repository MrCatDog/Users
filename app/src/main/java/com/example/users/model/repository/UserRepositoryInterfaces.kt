package com.example.users.model.repository

import androidx.lifecycle.LiveData
import com.example.users.model.database.DatabaseUser
import com.example.users.model.domain.FullUserInfo

interface UserRepository : UserDBRepository, UserNetworkRepository {

    val users: LiveData<List<FullUserInfo.BaseUserInfo>>
    val error: LiveData<ResultWrapper.Failure>

    suspend fun getUsers()
}

interface UserNetworkRepository {
    suspend fun loadUsresFromNetwork()
}

interface UserDBRepository {
    suspend fun loadUsersFromDB() : ResultWrapper<List<FullUserInfo>>
    suspend fun saveUsersInDB(users: List<FullUserInfo>)
}