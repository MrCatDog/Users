package com.example.users.mainfragment.model.repository

import com.example.users.mainfragment.model.domainmodel.FullUserInfo

interface UserDBRepository {
    suspend fun loadUsersFromDB() : List<FullUserInfo>
}