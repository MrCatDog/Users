package com.example.users.utils.network

import com.example.users.mainfragment.model.dto.NetworkUser
import retrofit2.*
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

class DataReceiver(private val serverApi: ServerApi) {

    fun requestUsers(
        onResponse: KFunction1<Response<List<NetworkUser>>, Unit>,
        onFail: KFunction2<Call<List<NetworkUser>>, Throwable, Unit>
    ) {
        serverApi.getHotelList().enqueue(object : Callback<List<NetworkUser>> {

            override fun onFailure(call: Call<List<NetworkUser>>, t: Throwable) {
                onFail(call, t)
            }

            override fun onResponse(
                call: Call<List<NetworkUser>>,
                response: Response<List<NetworkUser>>
            ) {
                onResponse(response)
            }
        })
    }
}