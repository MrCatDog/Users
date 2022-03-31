package com.example.users.mainfragment.model.repository

import com.example.users.mainfragment.model.domainmodel.FullUserInfo

interface UserDBRepository {
    fun loadUsersFromDB() : List<FullUserInfo>
}