package com.yomi.next2go.core.network.error

import com.yomi.next2go.core.domain.model.DataError
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class NetworkErrorMapperTest {

    @Test
    fun toDataError_unknownHostException_returnsNetworkUnavailable() {
        val exception = UnknownHostException("Host not found")
        
        val result = exception.toDataError()
        
        assertEquals(DataError.NetworkUnavailable, result)
    }

    @Test
    fun toDataError_connectException_returnsNetworkUnavailable() {
        val exception = ConnectException("Connection refused")
        
        val result = exception.toDataError()
        
        assertEquals(DataError.NetworkUnavailable, result)
    }

    @Test
    fun toDataError_socketTimeoutException_returnsTimeout() {
        val exception = SocketTimeoutException("Connection timed out")
        
        val result = exception.toDataError()
        
        assertEquals(DataError.Timeout, result)
    }

    @Test
    fun toDataError_ioException_returnsNetworkUnavailable() {
        val exception = IOException("IO error")
        
        val result = exception.toDataError()
        
        assertEquals(DataError.NetworkUnavailable, result)
    }

    @Test
    fun toDataError_httpException500_returnsServerError() {
        val response = Response.error<String>(500, "Server Error".toResponseBody())
        val exception = HttpException(response)
        
        val result = exception.toDataError()
        
        assertEquals(DataError.ServerError, result)
    }

    @Test
    fun toDataError_httpException503_returnsServerError() {
        val response = Response.error<String>(503, "Service Unavailable".toResponseBody())
        val exception = HttpException(response)
        
        val result = exception.toDataError()
        
        assertEquals(DataError.ServerError, result)
    }

    @Test
    fun toDataError_httpException404_returnsHttpError() {
        val response = Response.error<String>(404, "Not Found".toResponseBody())
        val exception = HttpException(response)
        
        val result = exception.toDataError()
        
        assertTrue(result is DataError.HttpError)
        val httpError = result as DataError.HttpError
        assertEquals(404, httpError.code)
        assertEquals("HTTP 404 Response.error()", httpError.message)
    }

    @Test
    fun toDataError_httpException401_returnsHttpError() {
        val response = Response.error<String>(401, "Unauthorized".toResponseBody())
        val exception = HttpException(response)
        
        val result = exception.toDataError()
        
        assertTrue(result is DataError.HttpError)
        val httpError = result as DataError.HttpError
        assertEquals(401, httpError.code)
    }

    @Test
    fun toDataError_unknownException_returnsUnknown() {
        val exception = RuntimeException("Something went wrong")
        
        val result = exception.toDataError()
        
        assertTrue(result is DataError.Unknown)
        val unknownError = result as DataError.Unknown
        assertEquals(exception, unknownError.cause)
    }

    @Test
    fun toDataError_nullPointerException_returnsUnknown() {
        val exception = NullPointerException("Null value")
        
        val result = exception.toDataError()
        
        assertTrue(result is DataError.Unknown)
        val unknownError = result as DataError.Unknown
        assertEquals(exception, unknownError.cause)
    }
}