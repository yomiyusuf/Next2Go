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
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RaceRepositoryImplTest {

    @Test
    fun getNextToGoRaces_withValidData_returnsSuccess() = runTest {
        val raceDto = RaceDto(
            raceId = "race-1",
            raceName = "Test Race",
            raceNumber = 1,
            meetingName = "Test Meeting",
            // HORSE
            categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae",
            advertisedStart = AdvertisedStartDto(seconds = 1762340700),
        )
        val apiResponse = ApiResponse(
            status = 200,
            data = RacingData(
                nextToGoIds = listOf("race-1"),
                raceSummaries = mapOf("race-1" to raceDto),
            ),
            message = "OK",
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
                Response.error<Any>(500, "".toResponseBody()),
            )
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRaces(1)

        assertTrue(result is Result.Error)
        assertEquals(DataError.ServerError, (result as Result.Error).error)
    }

    @Test
    fun getNextToGoRaces_withEmptyCategories_callsApiWithoutCategoryFilter() = runTest {
        val raceDto = RaceDto(
            raceId = "race-1",
            raceName = "Test Race",
            raceNumber = 1,
            meetingName = "Test Meeting",
            categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae",
            advertisedStart = AdvertisedStartDto(seconds = 1762340700),
        )
        val apiResponse = ApiResponse(
            status = 200,
            data = RacingData(
                nextToGoIds = listOf("race-1"),
                raceSummaries = mapOf("race-1" to raceDto),
            ),
            message = "OK",
        )

        val mockApiService = mockk<RacingApiService> {
            coEvery { getNextRaces(count = 15, categoryId = null) } returns apiResponse
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRaces(count = 10, categories = emptySet())

        assertTrue(result is Result.Success)
        coVerify { mockApiService.getNextRaces(count = 15, categoryId = null) }
    }

    @Test
    fun getNextToGoRaces_withSingleCategory_callsApiWithCategoryFilter() = runTest {
        val raceDto = RaceDto(
            raceId = "race-1",
            raceName = "Test Race",
            raceNumber = 1,
            meetingName = "Test Meeting",
            categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae",
            advertisedStart = AdvertisedStartDto(seconds = 1762340700),
        )
        val apiResponse = ApiResponse(
            status = 200,
            data = RacingData(
                nextToGoIds = listOf("race-1"),
                raceSummaries = mapOf("race-1" to raceDto),
            ),
            message = "OK",
        )

        val mockApiService = mockk<RacingApiService> {
            coEvery { getNextRaces(count = 15, categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae") } returns apiResponse
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRaces(count = 10, categories = setOf(CategoryId.HORSE))

        assertTrue(result is Result.Success)
        coVerify { mockApiService.getNextRaces(count = 15, categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae") }
    }

    @Test
    fun getNextToGoRaces_withMultipleCategories_makesParallelApiCalls() = runTest {
        val horseRaceDto = RaceDto(
            raceId = "horse-race-1",
            raceName = "Horse Race",
            raceNumber = 1,
            meetingName = "Flemington",
            categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae",
            advertisedStart = AdvertisedStartDto(seconds = 1762340700),
        )
        val greyhoundRaceDto = RaceDto(
            raceId = "greyhound-race-1",
            raceName = "Greyhound Race",
            raceNumber = 2,
            meetingName = "The Meadows",
            categoryId = "9daef0d7-bf3c-4f50-921d-8e818c60fe61",
            advertisedStart = AdvertisedStartDto(seconds = 1762340800),
        )

        val horseApiResponse = ApiResponse(
            status = 200,
            data = RacingData(
                nextToGoIds = listOf("horse-race-1"),
                raceSummaries = mapOf("horse-race-1" to horseRaceDto),
            ),
            message = "OK",
        )
        val greyhoundApiResponse = ApiResponse(
            status = 200,
            data = RacingData(
                nextToGoIds = listOf("greyhound-race-1"),
                raceSummaries = mapOf("greyhound-race-1" to greyhoundRaceDto),
            ),
            message = "OK",
        )

        val mockApiService = mockk<RacingApiService> {
            coEvery { getNextRaces(count = 10, categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae") } returns horseApiResponse
            coEvery { getNextRaces(count = 10, categoryId = "9daef0d7-bf3c-4f50-921d-8e818c60fe61") } returns greyhoundApiResponse
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRaces(
            count = 10,
            categories = setOf(CategoryId.HORSE, CategoryId.GREYHOUND)
        )

        assertTrue(result is Result.Success)
        val races = result.data
        assertEquals(2, races.size)
        
        // Verify races are sorted by advertised start time
        assertEquals("horse-race-1", races[0].id)
        assertEquals("greyhound-race-1", races[1].id)
        
        // Verify both API calls were made
        coVerify { mockApiService.getNextRaces(count = 10, categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae") }
        coVerify { mockApiService.getNextRaces(count = 10, categoryId = "9daef0d7-bf3c-4f50-921d-8e818c60fe61") }
    }

    @Test
    fun getNextToGoRaces_withMultipleCategoriesPartialFailure_returnsSuccessfulRaces() = runTest {
        val horseRaceDto = RaceDto(
            raceId = "horse-race-1",
            raceName = "Horse Race",
            raceNumber = 1,
            meetingName = "Flemington",
            categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae",
            advertisedStart = AdvertisedStartDto(seconds = 1762340700),
        )

        val horseApiResponse = ApiResponse(
            status = 200,
            data = RacingData(
                nextToGoIds = listOf("horse-race-1"),
                raceSummaries = mapOf("horse-race-1" to horseRaceDto),
            ),
            message = "OK",
        )

        val mockApiService = mockk<RacingApiService> {
            coEvery { getNextRaces(count = 10, categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae") } returns horseApiResponse
            coEvery { getNextRaces(count = 10, categoryId = "9daef0d7-bf3c-4f50-921d-8e818c60fe61") } throws Exception("Network error")
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRaces(
            count = 10,
            categories = setOf(CategoryId.HORSE, CategoryId.GREYHOUND)
        )

        assertTrue(result is Result.Success)
        val races = result.data
        assertEquals(1, races.size)
        assertEquals("horse-race-1", races[0].id)
        assertEquals(CategoryId.HORSE, races[0].categoryId)
    }

    @Test
    fun getNextToGoRaces_withDuplicateRaceIds_removiesDuplicates() = runTest {
        val duplicateRaceDto = RaceDto(
            raceId = "duplicate-race",
            raceName = "Duplicate Race",
            raceNumber = 1,
            meetingName = "Test Meeting",
            categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae",
            advertisedStart = AdvertisedStartDto(seconds = 1762340700),
        )

        val horseApiResponse = ApiResponse(
            status = 200,
            data = RacingData(
                nextToGoIds = listOf("duplicate-race"),
                raceSummaries = mapOf("duplicate-race" to duplicateRaceDto),
            ),
            message = "OK",
        )
        val greyhoundApiResponse = ApiResponse(
            status = 200,
            data = RacingData(
                nextToGoIds = listOf("duplicate-race"),
                raceSummaries = mapOf("duplicate-race" to duplicateRaceDto),
            ),
            message = "OK",
        )

        val mockApiService = mockk<RacingApiService> {
            coEvery { getNextRaces(count = 10, categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae") } returns horseApiResponse
            coEvery { getNextRaces(count = 10, categoryId = "9daef0d7-bf3c-4f50-921d-8e818c60fe61") } returns greyhoundApiResponse
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRaces(
            count = 10,
            categories = setOf(CategoryId.HORSE, CategoryId.GREYHOUND)
        )

        assertTrue(result is Result.Success)
        val races = result.data
        assertEquals(1, races.size) // Duplicate should be removed
        assertEquals("duplicate-race", races[0].id)
    }

    @Test
    fun getNextToGoRaces_withMultipleCategories_sortsRacesByAdvertisedStartTime() = runTest {
        val laterRaceDto = RaceDto(
            raceId = "later-race",
            raceName = "Later Race",
            raceNumber = 2,
            meetingName = "Test Meeting",
            categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae", // Horse
            advertisedStart = AdvertisedStartDto(seconds = 1762340800), // Later time
        )
        val earlierRaceDto = RaceDto(
            raceId = "earlier-race",
            raceName = "Earlier Race",
            raceNumber = 1,
            meetingName = "Test Meeting",
            categoryId = "9daef0d7-bf3c-4f50-921d-8e818c60fe61", // Greyhound
            advertisedStart = AdvertisedStartDto(seconds = 1762340600), // Earlier time
        )

        val horseApiResponse = ApiResponse(
            status = 200,
            data = RacingData(
                nextToGoIds = listOf("later-race"),
                raceSummaries = mapOf("later-race" to laterRaceDto),
            ),
            message = "OK",
        )
        val greyhoundApiResponse = ApiResponse(
            status = 200,
            data = RacingData(
                nextToGoIds = listOf("earlier-race"),
                raceSummaries = mapOf("earlier-race" to earlierRaceDto),
            ),
            message = "OK",
        )

        val mockApiService = mockk<RacingApiService> {
            coEvery { getNextRaces(count = 10, categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae") } returns horseApiResponse
            coEvery { getNextRaces(count = 10, categoryId = "9daef0d7-bf3c-4f50-921d-8e818c60fe61") } returns greyhoundApiResponse
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRaces(
            count = 10,
            categories = setOf(CategoryId.HORSE, CategoryId.GREYHOUND)
        )

        assertTrue(result is Result.Success)
        val races = result.data
        assertEquals(2, races.size)
        
        // Should be sorted by advertised start time (earliest first)
        assertEquals("earlier-race", races[0].id)
        assertEquals("later-race", races[1].id)
    }

    @Test
    fun getNextToGoRacesStream_emitsDataFromGetNextToGoRaces() = runTest {
        val raceDto = RaceDto(
            raceId = "stream-race-1",
            raceName = "Stream Race",
            raceNumber = 1,
            meetingName = "Test Meeting",
            categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae",
            advertisedStart = AdvertisedStartDto(seconds = 1762340700),
        )
        val apiResponse = ApiResponse(
            status = 200,
            data = RacingData(
                nextToGoIds = listOf("stream-race-1"),
                raceSummaries = mapOf("stream-race-1" to raceDto),
            ),
            message = "OK",
        )

        val mockApiService = mockk<RacingApiService> {
            coEvery { getNextRaces(any(), any()) } returns apiResponse
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRacesStream(count = 10, categories = emptySet()).first()

        assertTrue(result is Result.Success)
        val races = result.data
        assertEquals(1, races.size)
        assertEquals("stream-race-1", races[0].id)
        assertEquals("Stream Race", races[0].name)
    }

    @Test
    fun getNextToGoRaces_withNetworkUnavailable_returnsNetworkUnavailableError() = runTest {
        val mockApiService = mockk<RacingApiService> {
            coEvery { getNextRaces(any(), any()) } throws UnknownHostException("Network unreachable")
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRaces(10)

        assertTrue(result is Result.Error)
        assertEquals(DataError.NetworkUnavailable, result.error)
    }

    @Test
    fun getNextToGoRaces_withConnectionTimeout_returnsTimeoutError() = runTest {
        val mockApiService = mockk<RacingApiService> {
            coEvery { getNextRaces(any(), any()) } throws SocketTimeoutException("Timeout")
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRaces(10)

        assertTrue(result is Result.Error)
        assertEquals(DataError.Timeout, result.error)
    }

    @Test
    fun getNextToGoRaces_withConnectionRefused_returnsNetworkUnavailableError() = runTest {
        val mockApiService = mockk<RacingApiService> {
            coEvery { getNextRaces(any(), any()) } throws ConnectException("Connection refused")
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRaces(10)

        assertTrue(result is Result.Error)
        assertEquals(DataError.NetworkUnavailable, result.error)
    }

    @Test
    fun getNextToGoRaces_withHttp400Error_returnsHttpError() = runTest {
        val mockApiService = mockk<RacingApiService> {
            coEvery { getNextRaces(any(), any()) } throws HttpException(
                Response.error<Any>(400, "Bad Request".toResponseBody())
            )
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRaces(10)

        assertTrue(result is Result.Error)
        assertEquals(DataError.HttpError(400, "Response.error()"), result.error)
    }

    @Test
    fun getNextToGoRaces_withHttp404Error_returnsHttpError() = runTest {
        val mockApiService = mockk<RacingApiService> {
            coEvery { getNextRaces(any(), any()) } throws HttpException(
                Response.error<Any>(404, "Not Found".toResponseBody())
            )
        }

        val repository = RaceRepositoryImpl(mockApiService)
        val result = repository.getNextToGoRaces(10)

        assertTrue(result is Result.Error)
        assertEquals(DataError.HttpError(404, "Response.error()"), result.error)
    }
}
