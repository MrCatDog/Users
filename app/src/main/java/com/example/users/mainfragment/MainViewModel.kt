package com.example.users.mainfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.users.mainfragment.model.domainmodel.FullUserInfo.BaseUserInfo
import com.example.users.mainfragment.model.domainmodel.FullUserInfo
import com.example.users.mainfragment.model.domainmodel.MainModel
import com.example.users.utils.MutableLiveEvent
import com.example.users.mainfragment.model.repository.ResultWrapper
import com.example.users.mainfragment.model.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

const val PARSE_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss Z"
const val FORMAT_DATE_PATTERN = "HH:mm dd.MM.yy"

class MainViewModel : ViewModel() {

    @Inject
    lateinit var repository: UserRepository

    private val _users = MutableLiveData<List<BaseUserInfo>>()
    val users: LiveData<List<BaseUserInfo>>
        get() = _users

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

    private val model = MainModel()
    private val userBackstack = ArrayDeque<Int>()

    fun loadOrRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val users = repository.loadUsers()) {
                is ResultWrapper.Success -> if (users.value.isEmpty()) {
                    loadUsers()
                } else {
                    updateData(users.value)
                }
                is ResultWrapper.Failure -> _errorText.postValue(users.error!!.message)
            }

        }
    }

    private suspend fun loadUsers() {
        _isLoading.postValue(true)
        when (val usersAnswer = repository.updateUsers()) {
            is ResultWrapper.Success -> updateData(usersAnswer.value)
            is ResultWrapper.Failure -> _errorText.postValue(usersAnswer.error!!.message) //todo
        }
        repository.saveUsers(model.items)
        _isLoading.postValue(false)
    }

    private fun updateData(users: List<FullUserInfo>) {
        model.items = users as ArrayList<FullUserInfo>
        updateDisplayingUserInfo()
    }

    private fun updateDisplayingUserInfo() {
        if (_isUserVisible.value == true) {
            _selectedUser.postValue(findFullUserInfoById(_selectedUser.value!!.baseUserInfo.id))
        }
        formNewUsersList()
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
        viewModelScope.launch(Dispatchers.IO) {
            loadUsers()
        }
    }

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

    private fun findFullUserInfoById(id: Int) = model.items.find { it.baseUserInfo.id == id }

    fun backBtnPressed() {
        if (userBackstack.isEmpty()) {
            _isUserVisible.value = false
        } else {
            _selectedUser.value = findFullUserInfoById(userBackstack.pop())
        }
        formNewUsersList()
    }
}