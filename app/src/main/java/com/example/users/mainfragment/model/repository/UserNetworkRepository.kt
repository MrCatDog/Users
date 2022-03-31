package com.example.users.mainfragment.model.repository

import com.example.users.mainfragment.model.domainmodel.FullUserInfo

interface UserNetworkRepository {
    fun updateUsersFromNetwork() : List<FullUserInfo>
}