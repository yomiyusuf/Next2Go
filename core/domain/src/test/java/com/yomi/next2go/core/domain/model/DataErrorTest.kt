package com.yomi.next2go.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class DataErrorTest {

    @Test
    fun dataError_networkUnavailable_isDataErrorInstance() {
        val error = DataError.NetworkUnavailable
        assert(error is DataError)
    }

    @Test
    fun dataError_serverError_isDataErrorInstance() {
        val error = DataError.ServerError
        assert(error is DataError)
    }

    @Test
    fun dataError_timeout_isDataErrorInstance() {
        val error = DataError.Timeout
        assert(error is DataError)
    }

    @Test
    fun dataError_httpError_containsCodeAndMessage() {
        val error = DataError.HttpError(404, "Not Found")
        assertEquals(404, error.code)
        assertEquals("Not Found", error.message)
    }

    @Test
    fun dataError_parseError_containsCause() {
        val cause = RuntimeException("Parse failed")
        val error = DataError.ParseError(cause)
        assertEquals(cause, error.cause)
    }

    @Test
    fun dataError_unknown_containsCause() {
        val cause = RuntimeException("Unknown error")
        val error = DataError.Unknown(cause)
        assertEquals(cause, error.cause)
    }

    @Test
    fun dataError_unknown_canHaveNullCause() {
        val error = DataError.Unknown(null)
        assertEquals(null, error.cause)
    }

    @Test
    fun dataError_httpErrors_withDifferentCodes_areNotEqual() {
        val error1 = DataError.HttpError(404, "Not Found")
        val error2 = DataError.HttpError(500, "Server Error")
        assertNotEquals(error1, error2)
    }

    @Test
    fun dataError_sameHttpErrors_areEqual() {
        val error1 = DataError.HttpError(404, "Not Found")
        val error2 = DataError.HttpError(404, "Not Found")
        assertEquals(error1, error2)
    }
}