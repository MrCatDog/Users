package com.example.users.mainfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.users.utils.network.DataReceiver
import com.example.users.utils.network.UserResponse
import retrofit2.Call
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val dataReceiver = DataReceiver()

    private val _users = MutableLiveData<List<BaseUserInfo>>()
    val users: LiveData<List<BaseUserInfo>>
        get() = _users

    init {
        //todo проверить базу на существование если нет, грузим
        loadUsers()
    }

    private fun loadUsers() {
        //_visibility.value = Visibility.LOADING
        dataReceiver.requestUsers(this::onMainResponse, this::onMainFailure)
    }

    private fun onMainFailure(call: Call<List<UserResponse>>, e: Throwable) {
        if (!call.isCanceled) {
            setError(e.message)
//            _err.postValue(e.message)
//            _visibility.postValue(Visibility.ERROR)
        }
    }

    private fun onMainResponse(response: Response<List<UserResponse>>) {
        if (response.isSuccessful) {
            val users: List<FullUserInfo>? = response.body()?.map {
                FullUserInfo(
                    id = it.id,
                    name = it.name,
                    address = it.address,
                    stars = it.stars,
                    distance = it.distance,
                    suites = it.suites.trim(DELIMITER).split(DELIMITER)
                )
            }
            if (users != null) {
                // ошибка на hotels (Expected non-nullable value), но всё компилится. Ошибка среды?
                _users.postValue(users)
                //_visibility.postValue(Visibility.HOTELS)
            } else {
                setError(response.errorBody().toString())
            }
//Вариант без ошибки и, вроде как, более Kotlin-style, хотя читаемость упала, WTF вызывет.
//apply заменяет let, подробнее: https://dev.to/vlazdra/a-decompiled-story-of-kotlin-let-and-run-4k83
//            hotels?.apply {
//                _hotels.postValue(this)
//                _visibility.postValue(Visibility.HOTELS)
//            } ?: setError(response.errorBody().toString())
        } else {
            setError(response.errorBody().toString())
        }
    }

    private fun setError(text: String?) {
        _err.postValue(text)
        _visibility.postValue(Visibility.ERROR)
    }

    fun refreshBtnClicked() {
        loadUsers()
    }

}