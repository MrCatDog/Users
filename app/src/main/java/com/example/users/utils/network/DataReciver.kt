package com.example.users.utils.network

import retrofit2.*
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

class DataReceiver(private val serverApi: ServerApi) {

    fun requestUsers(
        onResponse: KFunction1<Response<List<UserResponse>>, Unit>,
        onFail: KFunction2<Call<List<UserResponse>>, Throwable, Unit>
    ) {
        serverApi.getHotelList().enqueue(object : Callback<List<UserResponse>> {

            override fun onFailure(call: Call<List<UserResponse>>, t: Throwable) {
                onFail(call, t)
            }

            override fun onResponse(
                call: Call<List<UserResponse>>,
                response: Response<List<UserResponse>>
            ) {
                onResponse(response)
            }
        })
    }
}