package com.yomi.next2go.core.network.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RacingApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: RacingApiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
            
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RacingApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getNextRaces_validResponse_returnsApiResponse() = runTest {
        // Given
        val mockResponse = """
            {
                "status": 200,
                "data": {
                    "next_to_go_ids": ["race-1", "race-2"],
                    "race_summaries": {
                        "race-1": {
                            "race_id": "race-1",
                            "race_name": "Test Race",
                            "race_number": 1,
                            "meeting_name": "Test Meeting",
                            "category_id": "4a2788f8-e825-4d36-9894-efd4baf1cfae",
                            "advertised_start": {
                                "seconds": 1762340700
                            }
                        }
                    }
                },
                "message": "Success"
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
        )

        // When
        val result = apiService.getNextRaces()

        // Then
        assertEquals(200, result.status)
        assertEquals("Success", result.message)
        assertEquals(2, result.data.nextToGoIds.size)
        assertEquals("race-1", result.data.nextToGoIds[0])
        assertEquals("race-2", result.data.nextToGoIds[1])
        
        val race = result.data.raceSummaries["race-1"]!!
        assertEquals("race-1", race.raceId)
        assertEquals("Test Race", race.raceName)
        assertEquals(1, race.raceNumber)
        assertEquals("Test Meeting", race.meetingName)
        assertEquals("4a2788f8-e825-4d36-9894-efd4baf1cfae", race.categoryId)
        assertEquals(1762340700L, race.advertisedStart.seconds)
    }

    @Test
    fun getNextRaces_customParameters_sendsCorrectRequest() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"status": 200, "data": {"next_to_go_ids": [], "race_summaries": {}}, "message": "Success"}""")
        )

        // When
        apiService.getNextRaces(method = "nextraces", count = 5)

        // Then
        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/rest/v1/racing/?method=nextraces&count=5", request.path)
    }
}