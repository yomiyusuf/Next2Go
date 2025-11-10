package com.yomi.next2go.core.data.repository

import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.DataError
import com.yomi.next2go.core.domain.repository.Result
import com.yomi.next2go.core.network.api.RacingApiService
import com.yomi.next2go.core.network.dto.AdvertisedStartDto
import com.yomi.next2go.core.network.dto.ApiResponse
import com.yomi.next2go.core.network.dto.RaceDto
import com.yomi.next2go.core.network.dto.RacingData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

class RaceRepositoryImplTest {

    @Test
    fun getNextToGoRaces_withValidData_returnsSuccess() = runTest {
        val raceDto = RaceDto(
            raceId = "race-1",
            raceName = "Test Race",
            raceNumber = 1,
            meetingName = "Test Meeting",
            categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae", // HORSE
            advertisedStart = AdvertisedStartDto(seconds = 1762340700)
        )
        val apiResponse = ApiResponse(
            status = 200,
            data = RacingData(
                nextToGoIds = listOf("race-1"),
                raceSummaries = mapOf("race-1" to raceDto)
            ),
            message = "OK"
        )

        val mockApiService = mockk<RacingApiService> {
            coEvery { getNextRaces(any(), any()) } returns apiResponse
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRaces(1)

        assertTrue(result is Result.Success)
        val races = (result as Result.Success).data
        assertEquals(1, races.size)
        assertEquals("race-1", races.first().id)
        assertEquals("Test Race", races.first().name)
        assertEquals(CategoryId.HORSE, races.first().categoryId)
    }

    @Test
    fun getNextToGoRaces_withHttpError_returnsError() = runTest {
        val mockApiService = mockk<RacingApiService> {
            coEvery { getNextRaces(any(), any()) } throws HttpException(
                Response.error<Any>(500, "".toResponseBody())
            )
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRaces(1)

        assertTrue(result is Result.Error)
        assertEquals(DataError.ServerError, (result as Result.Error).error)
    }

}