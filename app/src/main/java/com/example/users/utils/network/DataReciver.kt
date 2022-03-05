package com.example.users.utils.network

import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

const val BASE_URL = "https://firebasestorage.googleapis.com"

class DataReceiver {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val serverApi: ServerApi = retrofit.create(ServerApi::class.java)

    fun requestUsers(
        onResponse: KFunction1<Response<List<UserResponse>>, Unit>,
        onFail: KFunction2<Call<List<UserResponse>>, Throwable, Unit>
    ) {
        val users = serverApi.getHotelList()

        users.enqueue(object : Callback<List<UserResponse>> {

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