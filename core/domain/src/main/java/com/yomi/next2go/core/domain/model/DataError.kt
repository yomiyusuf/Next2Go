package com.yomi.next2go.core.domain.model

sealed class DataError : Exception() {
    abstract override val message: String
    
    data object NetworkUnavailable : DataError() {
        override val message: String = "Network unavailable"
    }
    
    data object ServerError : DataError() {
        override val message: String = "Server error"
    }
    
    data object Timeout : DataError() {
        override val message: String = "Request timeout"
    }
    
    data class HttpError(val code: Int, override val message: String) : DataError()
    
    data class ParseError(override val cause: Throwable) : DataError() {
        override val message: String = "Data parsing error"
    }
    
    data class Unknown(override val cause: Throwable?) : DataError() {
        override val message: String = "Unknown error occurred"
    }
}