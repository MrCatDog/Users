package com.example.users

import android.app.Application
import android.content.Context
import com.example.users.utils.di.AppComponent
import com.example.users.utils.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initializeDagger()
    }

    private fun initializeDagger() {
       appComponent = DaggerAppComponent.builder()
           .applicationContext(applicationContext)
           .build()
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> applicationContext.appComponent
    }