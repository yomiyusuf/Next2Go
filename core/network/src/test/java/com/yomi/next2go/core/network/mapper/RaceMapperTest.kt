package com.yomi.next2go.core.network.mapper

import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.network.dto.AdvertisedStartDto
import com.yomi.next2go.core.network.dto.RaceDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.datetime.Instant

class RaceMapperTest {

    @Test
    fun toDomain_validHorseRace_mapsCorrectly() {
        // Given
        val raceDto = RaceDto(
            raceId = "test-race-id",
            raceName = "Test Horse Race",
            raceNumber = 5,
            meetingName = "Test Meeting",
            categoryId = "4a2788f8-e825-4d36-9894-efd4baf1cfae", // Horse
            advertisedStart = AdvertisedStartDto(seconds = 1762340700L),
        )

        // When
        val result = raceDto.toDomain()

        // Then
        assertEquals("test-race-id", result?.id)
        assertEquals("Test Horse Race", result?.name)
        assertEquals(5, result?.number)
        assertEquals("Test Meeting", result?.meetingName)
        assertEquals(CategoryId.HORSE, result?.categoryId)
        assertEquals(Instant.fromEpochSeconds(1762340700L), result?.advertisedStart)
    }

    @Test
    fun toDomain_validGreyhoundRace_mapsCorrectly() {
        // Given
        val raceDto = RaceDto(
            raceId = "greyhound-race",
            raceName = "Greyhound Sprint",
            raceNumber = 3,
            meetingName = "Speedway",
            categoryId = "9daef0d7-bf3c-4f50-921d-8e818c60fe61", // Greyhound
            advertisedStart = AdvertisedStartDto(seconds = 1762341000L),
        )

        // When
        val result = raceDto.toDomain()

        // Then
        assertEquals(CategoryId.GREYHOUND, result?.categoryId)
        assertEquals("greyhound-race", result?.id)
    }

    @Test
    fun toDomain_validHarnessRace_mapsCorrectly() {
        // Given
        val raceDto = RaceDto(
            raceId = "harness-race",
            raceName = "Harness Challenge",
            raceNumber = 7,
            meetingName = "Trotting Park",
            categoryId = "161d9be2-e909-4326-8c2c-35ed71fb460b", // Harness
            advertisedStart = AdvertisedStartDto(seconds = 1762341300L),
        )

        // When
        val result = raceDto.toDomain()

        // Then
        assertEquals(CategoryId.HARNESS, result?.categoryId)
        assertEquals("harness-race", result?.id)
    }

    @Test
    fun toDomain_invalidCategoryId_returnsNull() {
        // Given
        val raceDto = RaceDto(
            raceId = "invalid-race",
            raceName = "Invalid Race",
            raceNumber = 1,
            meetingName = "Unknown Meeting",
            categoryId = "invalid-category-id",
            advertisedStart = AdvertisedStartDto(seconds = 1762340700L),
        )

        // When
        val result = raceDto.toDomain()

        // Then
        assertNull(result)
    }

    @Test
    fun toDomain_realApiData_mapsCorrectly() {
        // Given - Using real data from api_response.json
        val raceDto = RaceDto(
            raceId = "22e36327-6980-4836-8d8d-f1da0362e9b5",
            raceName = "Burwood Stud 2Yo 0 To 1 Win Mobile Pace",
            raceNumber = 8,
            meetingName = "Redcliffe",
            categoryId = "161d9be2-e909-4326-8c2c-35ed71fb460b", // Harness
            advertisedStart = AdvertisedStartDto(seconds = 1762340700L),
        )

        // When
        val result = raceDto.toDomain()

        // Then
        assertEquals("22e36327-6980-4836-8d8d-f1da0362e9b5", result?.id)
        assertEquals("Burwood Stud 2Yo 0 To 1 Win Mobile Pace", result?.name)
        assertEquals(8, result?.number)
        assertEquals("Redcliffe", result?.meetingName)
        assertEquals(CategoryId.HARNESS, result?.categoryId)
        assertEquals(Instant.fromEpochSeconds(1762340700L), result?.advertisedStart)
    }
}