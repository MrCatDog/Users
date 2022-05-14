package com.example.users.model.repository

import androidx.lifecycle.LiveData
import com.example.users.model.database.DatabaseUser
import com.example.users.model.domain.FullUserInfo

interface UserRepository : UserDBRepository, UserNetworkRepository {

    val users: LiveData<List<FullUserInfo.BaseUserInfo>>
    val error: LiveData<ResultWrapper.Failure>
    val detailedUserInfo: LiveData<FullUserInfo>

}

interface UserNetworkRepository {
    suspend fun loadUsersFromNetwork()
}

interface UserDBRepository {
    suspend fun loadBaseUsersInfoFromDB(idList: List<Int> = emptyList())
    suspend fun saveUsersInDB(users: List<FullUserInfo>)
    suspend fun loadUserDetails(id: Int)
}