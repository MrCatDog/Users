package com.example.users.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.users.model.domain.FullUserInfo
import com.example.users.model.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserDetailsViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    val user: LiveData<FullUserInfo> = Transformations.map(repository.detailedUserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadBaseUsersInfoFromDB(it.friends.toList()) //todo ебаааать а это точно должно работать???
        }
        return@map it
    }

    val friends: LiveData<List<FullUserInfo.BaseUserInfo>> = repository.users

    val errorText: LiveData<String?> = Transformations.map(repository.error) {
        it.error?.message
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadUserDetails()
        }
    }

    fun listItemClicked(item: FullUserInfo.BaseUserInfo) {
        if (item.isActive) {

        }
    }
}