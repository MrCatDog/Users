package com.example.users.mainfragment.model.repository

import com.example.users.mainfragment.model.domainmodel.FullUserInfo

interface UserDBRepository {
    suspend fun loadUsers(): ResultWrapper<List<FullUserInfo>>
    suspend fun saveUsers(users: List<FullUserInfo>)
}