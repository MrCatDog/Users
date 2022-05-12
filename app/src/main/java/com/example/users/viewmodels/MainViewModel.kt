package com.example.users.viewmodels

import androidx.lifecycle.*
import com.example.users.model.domain.FullUserInfo.BaseUserInfo
import com.example.users.model.domain.FullUserInfo
import com.example.users.model.domain.MainModel
import com.example.users.utils.MutableLiveEvent
import com.example.users.model.repository.ResultWrapper
import com.example.users.model.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class MainViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    private val _users = MutableLiveData<List<BaseUserInfo>>()
    val users: LiveData<List<BaseUserInfo>>
        get() = _users

    val users2 = Transformations.map(repository.users) {

    }

    private val _selectedUser = MutableLiveData<FullUserInfo>()
    val selectedUser: LiveData<FullUserInfo>
        get() = _selectedUser

    private val _isUserVisible = MutableLiveData(false)
    val isUserVisible: LiveData<Boolean>
        get() = _isUserVisible

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _errorText = MutableLiveEvent<String?>()
    val errorText: LiveData<String?>
        get() = _errorText

    //todo: убрать эту убогую модель, список вынести в репозиторий, обновлять его по вызову отсюда, пробрасывать через LiveData в UI
    private val model = MainModel()
    private val userBackstack = ArrayDeque<Int>()

    init {
        getUsers()
    }

    private fun getUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            repository.getUsers()
            //handleAnswer(repositoryFunction.invoke())
            _isLoading.postValue(false)
        }
    }

    private fun handleAnswer(answer : ResultWrapper<List<FullUserInfo>>) {
        when (answer) {
            is ResultWrapper.Success -> updateDisplayingData(answer.value)
            is ResultWrapper.Failure -> _errorText.postValue(answer.error?.message)
        }
    }

    private fun updateDisplayingData(users: List<FullUserInfo>) {
        updateUsers(users)
        updateDisplayingUserInfo()
        formNewUsersList()
    }

    private fun updateUsers(newUsers: List<FullUserInfo>) {
        model.items = newUsers as ArrayList<FullUserInfo>
    }

    private fun updateDisplayingUserInfo() {
        if (_isUserVisible.value == true) {
            _selectedUser.postValue(findFullUserInfoById(_selectedUser.value!!.baseUserInfo.id)) //todo а если такого пользователя нет?
        }
    }

    private fun formNewUsersList() {
        _users.postValue(
            if (_isUserVisible.value == true) {
                model.items.filter { selectedUser.value!!.friends.contains(it.baseUserInfo.id) }
                    .map { it.baseUserInfo }
            } else {
                model.items.map { it.baseUserInfo }
            }
        )
    }

    fun refreshBtnClicked() {
        getUsers(repository::updateUsers)
    }

    private fun findFullUserInfoById(id: Int) = model.items.find { it.baseUserInfo.id == id }

    fun listItemClicked(item: BaseUserInfo) {
        if (item.isActive) {
            if (_isUserVisible.value == true) {
                userBackstack.push(_selectedUser.value!!.baseUserInfo.id)
            }
            _selectedUser.value = findFullUserInfoById(item.id)
            _isUserVisible.value = true
            formNewUsersList()
        }
    }

    fun backBtnPressed() {
        if (userBackstack.isEmpty()) {
            _isUserVisible.value = false
        } else {
            _selectedUser.value = findFullUserInfoById(userBackstack.pop())
        }
        formNewUsersList()
    }
}