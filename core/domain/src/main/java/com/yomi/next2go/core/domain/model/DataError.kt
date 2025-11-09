package com.yomi.next2go.core.domain.model

sealed class DataError : Exception() {
    data object NetworkUnavailable : DataError()
    data object ServerError : DataError()
    data object Timeout : DataError()
    data class HttpError(val code: Int, override val message: String) : DataError()
    data class ParseError(override val cause: Throwable) : DataError()
    data class Unknown(override val cause: Throwable?) : DataError()
}