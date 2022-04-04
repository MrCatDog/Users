package com.example.users.mainfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.users.R
import com.example.users.mainfragment.model.domainmodel.FullUserInfo.BaseUserInfo
import com.example.users.mainfragment.model.domainmodel.FullUserInfo
import com.example.users.mainfragment.model.domainmodel.MainModel
import com.example.users.utils.MutableLiveEvent
import com.example.users.utils.cachedatabase.UserDao
import com.example.users.utils.network.DataReceiver
import com.example.users.mainfragment.model.dto.NetworkUser
import com.example.users.mainfragment.model.repository.ResultWrapper
import com.example.users.mainfragment.model.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

const val PARSE_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss Z"
const val FORMAT_DATE_PATTERN = "HH:mm dd.MM.yy"

class MainViewModel : ViewModel() {

    @Inject lateinit var dataReceiver : DataReceiver
    @Inject lateinit var cache: UserDao
    @Inject lateinit var repository: UserRepository

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
            val users = cache.getAll()
            if (users.isEmpty()) {
                //loadUsers()
                when(val usersAnswer = repository.updateUsersFromNetwork()) {
                    is ResultWrapper.Success -> updateData(usersAnswer.value)
                    is ResultWrapper.Failure -> _errorText.postValue(usersAnswer.error!!.message) //todo
                }
            } else {
                model.items = users as ArrayList<FullUserInfo>
                formNewUsersList()
            }
        }
    }

//    private fun loadUsers() {
//        _isLoading.postValue(true)
//        dataReceiver.requestUsers(this::onMainResponse, this::onMainFailure)
//    }
//
//    private fun onMainResponse(response: Response<List<NetworkUser>>) {
//        if (response.isSuccessful) {
//            val responseBody = response.body()
//            if(responseBody != null) {
//                updateData(transformResponseToModel(responseBody))
//            } else {
//                _errorText.postValue(response.errorBody().toString())
//            }
//        } else {
//            _errorText.postValue(response.errorBody().toString())
//        }
//        _isLoading.value = false
//    }
//
//    private fun onMainFailure(call: Call<List<NetworkUser>>, e: Throwable) {
//        if (!call.isCanceled) {
//            _errorText.postValue(e.message)
//        }
//        _isLoading.value = false
//    }
//
//    private fun transformResponseToModel(body: List<NetworkUser>): List<FullUserInfo> = body.map {
//        FullUserInfo(
//            guid = it.guid,
//            baseUserInfo = BaseUserInfo(
//                id = it.id,
//                name = it.name,
//                email = it.email,
//                isActive = it.isActive
//            ),
//            age = it.age,
//            eyeColor = when (it.eyeColor) {
//                "brown" -> R.color.user_eye_color_brown
//                "blue" -> R.color.user_eye_color_blue
//                "green" -> R.color.user_eye_color_green
//                else -> R.color.white
//            },
//            company = it.company,
//            phone = it.phone,
//            address = it.address,
//            about = it.about,
//            favoriteFruit = when (it.favoriteFruit) {
//                "apple" -> R.string.user_fav_fruit_apple
//                "banana" -> R.string.user_fav_fruit_banana
//                "strawberry" -> R.string.user_fav_fruit_strawberry
//                else -> R.string.user_fav_fruit_unknown
//            },
//            registeredDate = transformDate(it.registered),
//            lat = it.latitude,
//            lon = it.longitude,
//            friends = it.friends.map { friend -> friend.id }.toSet()
//        )
//    }

    private fun updateData(users: List<FullUserInfo>) {
        model.items = users as ArrayList<FullUserInfo>
        updateDisplayingInfo()
        updateCache()
    }

    private fun updateDisplayingInfo() {
        if (_isUserVisible.value == true) {
            _selectedUser.value =
                findFullUserInfoById(_selectedUser.value!!.baseUserInfo.id)
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

    private fun updateCache() {
        viewModelScope.launch(Dispatchers.IO) {
            cache.cleanTable()
            cache.insertAll(model.items)
        }
    }

//    private fun transformDate(textDate: String) = formatDate(
//        try {
//            parseDate(textDate)!! // "In case of error, returns null." but in case of error i go into the catch block :\
//        } catch (ex: Exception) {
//            _errorText.postValue(ex.message)
//            Calendar.getInstance().time
//        }
//    )
//
//    private fun parseDate(textDate: String) =
//        SimpleDateFormat(PARSE_DATE_PATTERN, Locale.US).parse(textDate)
//
//    private fun formatDate(date: Date) =
//        SimpleDateFormat(FORMAT_DATE_PATTERN, Locale.getDefault()).format(date).toString()

    fun refreshBtnClicked() {
//        loadUsers()
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