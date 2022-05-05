package com.example.users.model.repository

import com.example.users.model.domain.FullUserInfo

interface UserRepository : UserDBRepository, UserNetworkRepository {
    suspend fun getUsers() : ResultWrapper<List<FullUserInfo>>
}

interface UserNetworkRepository {
    suspend fun updateUsers() : ResultWrapper<List<FullUserInfo>>
}

interface UserDBRepository {
    suspend fun loadUsers(): ResultWrapper<List<FullUserInfo>>
    suspend fun saveUsers(users: List<FullUserInfo>)
}