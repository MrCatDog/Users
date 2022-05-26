package com.example.users.viewmodels

import androidx.lifecycle.*
import com.example.users.model.domain.FullUserInfo.BaseUserInfo
import com.example.users.model.repository.ResultWrapper
import com.example.users.utils.MutableLiveEvent
import com.example.users.model.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UsersListViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    private val _users = MutableLiveData<List<BaseUserInfo>>()
    val users: LiveData<List<BaseUserInfo>>
        get() = _users

    private val _error = MutableLiveEvent<String>()
    val error: LiveData<String>
        get() = _error

    private val _navigateToUserDetails = MutableLiveEvent<Int>()
    val navigateToUserDetails: LiveData<Int>
        get() = _navigateToUserDetails

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        getUsers()
    }

    private fun getUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            //todo Качать из инета, если в первый раз (SharedPref?) и с ДБ во всех остальных случаях.
            updateUsersFromNetwork()
            getUsersFromDB()
            _isLoading.postValue(false)
        }
    }

    private suspend fun getUsersFromDB() {
        when (val answer = repository.loadBaseUsersInfoFromDB()) {
            is ResultWrapper.Success -> _users.postValue(answer.value!!)
            is ResultWrapper.Failure -> _error.postValue(answer.error?.message)
        }
    }

    private suspend fun updateUsersFromNetwork() {
        when (val answer = repository.loadUsersFromNetwork()) {
            is ResultWrapper.Success -> repository.saveUsersInDB(answer.value)
            is ResultWrapper.Failure -> _error.postValue(answer.error?.message)
        }
    }

    fun refreshBtnClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            updateUsersFromNetwork()
            _isLoading.postValue(false)
        }
    }

    fun listItemClicked(item: BaseUserInfo) {
        if (item.isActive) {
            _navigateToUserDetails.postValue(item.id)
        }
    }
}