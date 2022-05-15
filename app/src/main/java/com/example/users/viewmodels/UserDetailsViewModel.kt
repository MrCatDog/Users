package com.example.users.viewmodels

import androidx.lifecycle.*
import androidx.navigation.navArgument
import com.example.users.model.domain.FullUserInfo
import com.example.users.model.repository.UserRepository
import com.example.users.ui.UserDetailsFragmentArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserDetailsViewModel @Inject constructor(
    private val repository: UserRepository,
    private val state: SavedStateHandle
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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadUserDetails(state.get<Int>("userId")) //todo да блять, нахуй ваще нужны safeArgs если я тут опять залупу полуаю
        }
    }

    fun listItemClicked(item: FullUserInfo.BaseUserInfo) {
        if (item.isActive) {

        }
    }
}