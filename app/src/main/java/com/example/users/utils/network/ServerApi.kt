package com.example.users.utils.network

import com.example.users.mainfragment.model.dto.NetworkUser
import retrofit2.http.GET
import retrofit2.http.Query

const val DEFAULT_ALT = "media"
const val DEFAULT_TOKEN = "e3672c23-b1a5-4ca7-bb77-b6580d75810c"

interface ServerApi {
    @GET("v0/b/candidates--questionnaire.appspot.com/o/users.json")
    suspend fun getUserList(
        @Query("alt") alt: String = DEFAULT_ALT,
        @Query("token") token: String = DEFAULT_TOKEN
    ): List<NetworkUser>
}