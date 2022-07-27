package com.example.users.viewmodels

import android.net.Uri
import androidx.lifecycle.*
import com.example.users.R
import com.example.users.model.domain.FullUserInfo
import com.example.users.model.repository.ErrorEntity
import com.example.users.model.repository.ErrorEntity.*
import com.example.users.model.repository.ErrorEntity.ApiError.*
import com.example.users.model.repository.ErrorEntity.DBError.*
import com.example.users.model.repository.ResultWrapper
import com.example.users.model.repository.UserRepository
import com.example.users.utils.MutableLiveEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserDetailsViewModel @AssistedInject constructor(
    private val repository: UserRepository,
    @Assisted private val userId: Int
) : ViewModel() {

    companion object {
        const val navigateToMapPrefix = "geo:"
        const val navigateToMapDelimiter = ","
    }

    private val _user = MutableLiveData<FullUserInfo>()
    val user: LiveData<FullUserInfo>
        get() = _user

    private val _friends = MutableLiveData<List<FullUserInfo.BaseUserInfo>>()
    val friends: LiveData<List<FullUserInfo.BaseUserInfo>>
        get() = _friends

    private val _error = MutableLiveEvent<Int>()
    val errorText: LiveData<Int>
        get() = _error

    private val _navigateToUserDetails = MutableLiveEvent<Int>()
    val navigateToUserDetails: LiveData<Int>
        get() = _navigateToUserDetails

    private val _navigateToMap = MutableLiveEvent<Uri>()
    val navigateToMap: LiveData<Uri>
        get() = _navigateToMap

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getUserDetailedInfo(userId)
        }
    }

    private suspend fun getUserDetailedInfo(userId: Int) {
        when (val answer = repository.loadUserDetails(userId)) {
            is ResultWrapper.Success -> {
                answer.value.apply {
                    getFriends(this.friends.toList())
                    _user.postValue(this)
                }
            }
            is ResultWrapper.Failure -> _error.postValue(handleError(answer.error))
        }
    }

    private suspend fun getFriends(friendsIdList: List<Int>) {
        when (val friendsAnswer = repository.loadBaseUsersInfoFromDB(friendsIdList)) {
            is ResultWrapper.Success -> this._friends.postValue(friendsAnswer.value) //"this" required because of old lint bag
            is ResultWrapper.Failure -> _error.postValue(handleError(friendsAnswer.error))
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


    fun listItemClicked(item: FullUserInfo.BaseUserInfo) {
        if (item.isActive) {
            _navigateToUserDetails.postValue(item.id)
        }
    }

    fun userAddressClicked(location: FullUserInfo.Location) {
        _navigateToMap.postValue(
            Uri.parse(
                navigateToMapPrefix + location.latitude + navigateToMapDelimiter + location.longitude
            )
        )
    }
}

@AssistedFactory
interface UserDetailsViewModelFactory {
    fun create(userId: Int): UserDetailsViewModel
}