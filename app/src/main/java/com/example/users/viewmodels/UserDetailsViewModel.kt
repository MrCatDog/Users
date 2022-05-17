package com.example.users.viewmodels

import androidx.lifecycle.*
import com.example.users.model.domain.FullUserInfo
import com.example.users.model.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserDetailsViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

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

    private val _navigateToUserDetails = MutableLiveData<Int>()
    val navigateToUserDetails: LiveData<Int>
        get() = _navigateToUserDetails

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadUserDetails(1) //todo а если бы Hilt+SharedSaveState...1 просто для теста
        }
    }

    fun listItemClicked(item: FullUserInfo.BaseUserInfo) {
        if (item.isActive) {
            _navigateToUserDetails.postValue(item.id)
        }
    }
}