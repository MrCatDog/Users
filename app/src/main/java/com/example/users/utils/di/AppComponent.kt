package com.example.users.utils.di

import android.content.Context
import com.example.users.viewmodels.UserDetailsViewModelFactory
import com.example.users.viewmodels.UsersListViewModelFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RemoteModule::class, StorageModule::class, BinderModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(applicationContext: Context): Builder
        fun build(): AppComponent
    }
    fun provideUsersListViewModelFactory(): UsersListViewModelFactory
    fun provideUserDetailsViewModelFactory(): UserDetailsViewModelFactory
}



