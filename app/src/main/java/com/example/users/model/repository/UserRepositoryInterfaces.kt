package com.example.users.model.repository

import androidx.lifecycle.LiveData
import com.example.users.model.domain.FullUserInfo

interface UserRepository : UserDBRepository, UserNetworkRepository {

    val users: LiveData<ResultWrapper<List<FullUserInfo.BaseUserInfo>>>

    suspend fun getUsers()
}

interface UserNetworkRepository {
    suspend fun updateUsers()
}

interface UserDBRepository {
    suspend fun loadUsers(): ResultWrapper<List<FullUserInfo>>
    suspend fun saveUsers(users: List<FullUserInfo>)
}