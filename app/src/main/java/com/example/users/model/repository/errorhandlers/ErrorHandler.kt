package com.example.users.model.repository.errorhandlers

import com.example.users.model.repository.ErrorEntity

interface ErrorHandler {
    fun handleError(error: Throwable) : ErrorEntity
}