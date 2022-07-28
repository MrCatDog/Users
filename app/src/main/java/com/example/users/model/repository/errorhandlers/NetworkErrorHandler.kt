package com.example.users.model.repository.errorhandlers

import com.example.users.model.repository.ErrorEntity.ApiError.*
import com.example.users.model.repository.ErrorEntity
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection
import javax.inject.Inject

class NetworkErrorHandler @Inject constructor(): ErrorHandler {
    override fun handleError(error: Throwable): ErrorEntity {
        return when(error) {
            is IOException -> Network
            is HttpException -> {
                when (error.code()) {
                    // not found
                    HttpURLConnection.HTTP_NOT_FOUND -> NotFound

                    // access denied
                    HttpURLConnection.HTTP_FORBIDDEN -> AccessDenied

                    // unavailable service
                    HttpURLConnection.HTTP_UNAVAILABLE -> ServiceUnavailable

                    // all the others will be treated as unknown error
                    else -> ErrorEntity.UnknownError
                }
            }
            else -> ErrorEntity.UnknownError
        }
    }
}