package com.yomi.next2go.core.network.error

import com.yomi.next2go.core.domain.model.DataError
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toDataError(): DataError {
    return when (this) {
        is UnknownHostException, is ConnectException -> DataError.NetworkUnavailable
        is SocketTimeoutException -> DataError.Timeout
        is HttpException -> {
            when (code()) {
                in 500..599 -> DataError.ServerError
                else -> DataError.HttpError(code(), message())
            }
        }
        is IOException -> DataError.NetworkUnavailable
        else -> DataError.Unknown(this)
    }
}
