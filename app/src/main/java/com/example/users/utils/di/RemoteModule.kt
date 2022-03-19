package com.example.users.utils.di

import com.example.users.utils.network.DataReceiver
import com.example.users.utils.network.ServerApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class RemoteModule {

    //todo maybe i should throw it out of here
    private val baseUrl = "https://firebasestorage.googleapis.com"

    @Singleton
    @Provides
    fun provideNetworkUtils(serverApi: ServerApi): DataReceiver = DataReceiver(serverApi)

    @Singleton
    @Provides
    fun provideRetrofit(gson: GsonConverterFactory): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(gson)
        .build()

    @Singleton
    @Provides
    fun provideGson(): GsonConverterFactory = GsonConverterFactory.create()


    @Singleton
    @Provides
    fun provideServerApi(retrofit: Retrofit): ServerApi = retrofit.create(ServerApi::class.java)
}