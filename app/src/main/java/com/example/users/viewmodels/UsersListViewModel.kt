package com.example.users.viewmodels

import androidx.lifecycle.*
import com.example.users.model.domain.FullUserInfo.BaseUserInfo
import com.example.users.model.repository.ResultWrapper
import com.example.users.utils.MutableLiveEvent
import com.example.users.model.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsersListViewModel
@AssistedInject constructor(
    private val repository: UserRepository,
    @Assisted private val firstTimeLoading: Boolean
) : ViewModel() {

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

    private val _isFirstLoaded = MutableLiveEvent<Boolean>()
    val isFirstLoaded: LiveData<Boolean>
        get() = _isFirstLoaded

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            if (firstTimeLoading) {
                _isFirstLoaded.postValue(updateUsersFromNetwork())
            } else {
                getUsersFromDB()
            }
            _isLoading.postValue(false)
        }
    }

    private suspend fun getUsersFromDB() {
        when (val answer = repository.loadBaseUsersInfoFromDB()) {
            is ResultWrapper.Success -> _users.postValue(answer.value)
            is ResultWrapper.Failure -> _error.postValue(answer.error?.message)
        }
    }

    private suspend fun updateUsersFromNetwork(): Boolean {
        return when (val answer = repository.loadUsersFromNetwork()) {
            is ResultWrapper.Success -> {
                _users.postValue(answer.value.map { it.baseUserInfo })
                repository.saveUsersInDB(answer.value)
                false
            }
            is ResultWrapper.Failure -> {
                _error.postValue(answer.error?.message)
                true
            }
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

@AssistedFactory
interface UsersListViewModelFactory {
    fun create(firstTimeLoading: Boolean): UsersListViewModel
}