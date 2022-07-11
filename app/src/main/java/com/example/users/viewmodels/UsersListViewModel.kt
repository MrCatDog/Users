package com.example.users.viewmodels

import androidx.lifecycle.*
import com.example.users.R
import com.example.users.model.domain.FullUserInfo.BaseUserInfo
import com.example.users.utils.MutableLiveEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.users.model.repository.UserRepository
import com.example.users.model.repository.ResultWrapper
import com.example.users.model.repository.ErrorEntity
import com.example.users.model.repository.ErrorEntity.*
import com.example.users.model.repository.ErrorEntity.ApiError.*
import com.example.users.model.repository.ErrorEntity.DBError.*

class UsersListViewModel
@AssistedInject constructor(
    private val repository: UserRepository,
    @Assisted private val firstTimeLoading: Boolean
) : ViewModel() {

    private val _users = MutableLiveData<List<BaseUserInfo>>()
    val users: LiveData<List<BaseUserInfo>>
        get() = _users

    private val _error = MutableLiveEvent<Int>()
    val error: LiveData<Int>
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
            is ResultWrapper.Success -> this._users.postValue(answer.value) //"this" required because of old lint bag
            is ResultWrapper.Failure -> _error.postValue(handleError(answer.error))
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
                _error.postValue(handleError(answer.error))
                true
            }
        }
    }

    private fun handleError(error: ErrorEntity): Int {
        return when (error) {
            is ApiError -> when (error) {
                Network -> R.string.network_error_text
                NotFound -> R.string.not_found_error_text
                AccessDenied -> R.string.access_denied_error_text
                ServiceUnavailable -> R.string.service_unavailable_error_text
            }
            is DBError -> when (error) {
                NoPermission -> R.string.no_permission_error_text
                Common -> R.string.common_db_error_text
            }
            is UnknownError -> R.string.unknown_error_text
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