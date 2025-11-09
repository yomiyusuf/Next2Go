package com.yomi.next2go.core.network.api

import com.yomi.next2go.core.network.dto.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RacingApiService {
    @GET("rest/v1/racing/")
    suspend fun getNextRaces(
        @Query("method") method: String = "nextraces",
        @Query("count") count: Int = 10,
    ): ApiResponse
}