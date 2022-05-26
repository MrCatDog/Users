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