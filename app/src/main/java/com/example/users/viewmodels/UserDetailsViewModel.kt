package com.example.users.viewmodels

import androidx.lifecycle.*
import com.example.users.model.domain.FullUserInfo
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

    private val _user = MutableLiveData<FullUserInfo>()
    val user: LiveData<FullUserInfo>
        get() = _user

    private val _friends = MutableLiveData<List<FullUserInfo.BaseUserInfo>>()
    val friends: LiveData<List<FullUserInfo.BaseUserInfo>>
        get() = _friends

    private val _error = MutableLiveEvent<String?>()
    val errorText: LiveData<String?>
        get() = _error

    private val _navigateToUserDetails = MutableLiveEvent<Int>()
    val navigateToUserDetails: LiveData<Int>
        get() = _navigateToUserDetails

    private val _navigateToMap = MutableLiveEvent<FullUserInfo.Location>()
    val navigateToMap: LiveData<FullUserInfo.Location>
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
            is ResultWrapper.Failure -> _error.postValue(answer.error?.message)
        }
    }

    private suspend fun getFriends(friendsIdList: List<Int>) {
        when (val friendsAnswer = repository.loadBaseUsersInfoFromDB(friendsIdList)) {
            is ResultWrapper.Success -> _friends.postValue(friendsAnswer.value) //todo да какого чёрта?
            is ResultWrapper.Failure -> _error.postValue(friendsAnswer.error?.message)
        }
    }

    fun listItemClicked(item: FullUserInfo.BaseUserInfo) {
        if (item.isActive) {
            _navigateToUserDetails.postValue(item.id)
        }
    }

    fun userAddressClicked(location: FullUserInfo.Location) {
        _navigateToMap.postValue(location)
    }
}

@AssistedFactory
interface UserDetailsViewModelFactory {
    fun create(userId: Int): UserDetailsViewModel
}