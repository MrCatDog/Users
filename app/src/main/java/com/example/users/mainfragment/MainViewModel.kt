package com.example.users.mainfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.users.R
import com.example.users.utils.MutableLiveEvent
import com.example.users.utils.network.DataReceiver
import com.example.users.utils.network.UserResponse
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel : ViewModel() {

    private val dataReceiver = DataReceiver()

    private val _users = MutableLiveData<List<BaseUserInfo>>()
    val users: LiveData<List<BaseUserInfo>>
        get() = _users

    private val _selectedUser = MutableLiveData<FullUserInfo?>()
    val selectedUser: LiveData<FullUserInfo?>
        get() = _selectedUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _errorText = MutableLiveEvent<String?>()
    val errorText: LiveData<String?>
        get() = _errorText

    private val model = MainModel()

    private val userBackstack = ArrayDeque<Int>()

    init {
        //todo проверить базу на существование если нет, грузим
        loadUsers()
    }

    private fun loadUsers() {
        _isLoading.value = true
        dataReceiver.requestUsers(this::onMainResponse, this::onMainFailure)
    }

    private fun onMainFailure(call: Call<List<UserResponse>>, e: Throwable) {
        if (!call.isCanceled) {
            _errorText.postValue(e.message)
            _isLoading.value = false
        }
    }

    private fun onMainResponse(response: Response<List<UserResponse>>) {
        if (response.isSuccessful) {
            val users: List<FullUserInfo>? = response.body()?.map {
                FullUserInfo(
                    baseUserInfo = BaseUserInfo(
                        id = it.id,
                        name = it.name,
                        email = it.email,
                        isActive = it.isActive
                    ),
                    age = it.age,
                    eyeColor = when (it.eyeColor) {
                        "brown" -> R.color.user_eye_color_brown
                        "blue" -> R.color.user_eye_color_blue
                        "green" -> R.color.user_eye_color_green
                        else -> R.color.white
                    },
                    company = it.company,
                    phone = it.phone,
                    address = it.address,
                    about = it.about,
                    favoriteFruit = when (it.favoriteFruit) {
                        "apple" -> R.string.user_fav_fruit_apple
                        "banana" -> R.string.user_fav_fruit_banana
                        "strawberry" -> R.string.user_fav_fruit_strawberry
                        else -> R.string.user_fav_fruit_unknown
                    },
                    registeredDate = transformDate(it.registered),
                    lat = it.latitude,
                    lon = it.longitude,
                    friends = it.friends.map {friend -> friend.id}.toSet()
                )
            }
            if (users != null) {
                model.items = users as ArrayList<FullUserInfo>
                _users.postValue(resetUsers())
            } else {
                _errorText.postValue(response.errorBody().toString())
            }
        } else {
            _errorText.postValue(response.errorBody().toString())
        }
        _isLoading.value = false
    }

    fun refreshBtnClicked() {
        loadUsers()
    }

    private fun transformDate(textDate: String): String {
        val date: Date? = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z", Locale.US).parse(textDate)
        return SimpleDateFormat("HH:mm dd.MM.yy", Locale.getDefault()).format(
            date ?: ""
        ).toString()

        //todo проверить работу этой штуки
    }

    fun listItemClicked(item: BaseUserInfo) {
        if (item.isActive) {
            val fullItemInfo = model.items.find { it.baseUserInfo.id == item.id }
            _selectedUser.value = fullItemInfo
            _users.value = resetUsers()
            _selectedUser.value.let {
                userBackstack.push(it.baseUserInfo.id) //todo какого хуя, ты же внутри let
                //todo нужен возврат на главный экран
            }
        }
    }

    private fun resetUsers() =
        model.items.filter { selectedUser.value?.friends?.contains(it.baseUserInfo.id) ?: true }
            .map { it.baseUserInfo }
    //todo ересь какая проверить и название

    private fun backBtnPressed() {
        if(!userBackstack.isEmpty()) {
            _selectedUser.value = model.items.find {it.baseUserInfo.id == userBackstack.pop()}
        } else {

        }

    }
}