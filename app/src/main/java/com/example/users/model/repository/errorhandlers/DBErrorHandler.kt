package com.example.users.model.repository.errorhandlers

import android.database.sqlite.SQLiteAccessPermException
import android.database.sqlite.SQLiteException
import com.example.users.model.repository.ErrorEntity
import javax.inject.Inject

class DBErrorHandler @Inject constructor() : ErrorHandler {
    override fun handleError(error: Throwable): ErrorEntity {
        return when(error) {
            is SQLiteAccessPermException -> ErrorEntity.DBError.NoPermission
            is SQLiteException -> ErrorEntity.DBError.Common
            else -> ErrorEntity.UnknownError
        }
    }
}