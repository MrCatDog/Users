package com.example.users.utils.di

import com.example.users.model.network.utils.ServerApi
import com.example.users.model.repository.errorhandlers.NetworkErrorHandler
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class RemoteModule {

    companion object {
        const val baseUrl = "https://firebasestorage.googleapis.com" //todo это точно тут должно быть?
    }

    @Singleton
    @Provides
    fun provideGson(): GsonConverterFactory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(gson: GsonConverterFactory): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(gson)
        .build()



    @Singleton
    @Provides
    fun provideServerApi(retrofit: Retrofit): ServerApi = retrofit.create(ServerApi::class.java)

    @Provides
    fun provideNetworkErrorHandler(): NetworkErrorHandler = NetworkErrorHandler()
}