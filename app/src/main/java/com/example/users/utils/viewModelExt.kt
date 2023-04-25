package com.example.users.utils

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

@MainThread
inline fun <reified VM : ViewModel> Fragment.viewModelsExt(
    crossinline creator: (handle: SavedStateHandle) -> VM
) = this.viewModels<VM> {
    object : AbstractSavedStateViewModelFactory(this@viewModelsExt, null) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return creator(handle) as T
        }
    }
}

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.viewModelsExt(
    crossinline creator: (handle: SavedStateHandle) -> VM
) = this.viewModels<VM> {
    object : AbstractSavedStateViewModelFactory(this@viewModelsExt, null) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            return creator(handle) as T
        }
    }
}