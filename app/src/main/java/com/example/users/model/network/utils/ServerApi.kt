package com.example.users.model.network.utils

import com.example.users.model.network.NetworkUser
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerApi {
    companion object {
        const val DEFAULT_ALT = "media"
        const val DEFAULT_TOKEN = "e3672c23-b1a5-4ca7-bb77-b6580d75810c"
    }

    @GET("v0/b/candidates--questionnaire.appspot.com/o/users.json")
    suspend fun getUserList(
        @Query("alt") alt: String = DEFAULT_ALT,
        @Query("token") token: String = DEFAULT_TOKEN
    ): List<NetworkUser>
}