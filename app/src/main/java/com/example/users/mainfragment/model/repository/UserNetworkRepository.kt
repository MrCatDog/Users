package com.example.users.mainfragment.model.repository

import com.example.users.mainfragment.model.domainmodel.FullUserInfo

interface UserNetworkRepository {
    suspend fun updateUsers() : ResultWrapper<List<FullUserInfo>>
}