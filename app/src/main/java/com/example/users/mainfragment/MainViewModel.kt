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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

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
                    address = it.address,
                    about = it.about,
                    favoriteFruit = when (it.favoriteFruit) {
                        "apple" -> R.string.user_fav_fruit_apple
                        "banana" -> R.string.user_fav_fruit_banana
                        "strawberry" -> R.string.user_fav_fruit_strawberry
                        else -> R.string.user_fav_fruit_unknown
                    },
                    registeredDate = transformDate(it.registered),
//                    LocalDate.parse(
//                        "14-02-2018",
//                        DateTimeFormatter.ofPattern("dd-MM-yyyy")
//                    ).toString(),
                    lat = it.latitude,
                    lon = it.longitude,
                    friends = it.friends
                )
            }
            if (users != null) {
                    model.items.addAll(users)
                _users.postValue(model.items.map {it.baseUserInfo})
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

    fun transformDate(textDate: String): String {
        val date: Date? = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z", Locale.US).parse(textDate)
        return SimpleDateFormat("HH:mm dd.MM.yy", Locale.getDefault()).format(
            date ?: ""
        ).toString()

        //todo проверить работу этой штуки
    }

    fun listItemClicked(item: BaseUserInfo) {
        if(item.isActive) {
            _selectedUser.value = model.items.first {it.baseUserInfo.id == item.id}
        }
    }

}