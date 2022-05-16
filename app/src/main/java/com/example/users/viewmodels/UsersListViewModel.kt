package com.example.users.viewmodels

import androidx.lifecycle.*
import com.example.users.model.domain.FullUserInfo.BaseUserInfo
import com.example.users.utils.MutableLiveEvent
import com.example.users.model.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UsersListViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    val users: LiveData<List<BaseUserInfo>> = repository.users

    val errorText: LiveData<String?> = Transformations.map(repository.error) {
        it.error?.message
    }

    private val _navigateToUserDetails = MutableLiveEvent<Int>()
    val navigateToUserDetails : LiveData<Int>
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
            repository.loadUsersFromNetwork()
            repository.loadBaseUsersInfoFromDB() //todo Качать из инета, если в первый раз (SharedPref?) и с ДБ во всех остальных случаях.
            _isLoading.postValue(false)
        }
    }

    fun refreshBtnClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadUsersFromNetwork()
        }
    }

    fun listItemClicked(item: BaseUserInfo) {
        if (item.isActive) {
            _navigateToUserDetails.postValue(item.id)
        }
    }
}