package com.yomi.next2go.core.domain.usecase

import com.yomi.next2go.core.common.time.Clock
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.DataError
import com.yomi.next2go.core.domain.model.Race
import com.yomi.next2go.core.domain.repository.RaceRepository
import com.yomi.next2go.core.domain.repository.Result
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetNextRacesUseCaseTest {

    @Test
    fun execute_withValidRaces_returnsFilteredAndSortedRaces() = runTest {
        val now = Instant.fromEpochSeconds(1000)
        val mockClock = mockk<Clock> {
            every { now() } returns now
        }

        val race1 = Race(
            id = "1",
            name = "Race 1",
            number = 1,
            meetingName = "Meeting 1",
            categoryId = CategoryId.HORSE,
            advertisedStart = Instant.fromEpochSeconds(1300), // 5 minutes future
        )
        
        val race2 = Race(
            id = "2", 
            name = "Race 2",
            number = 2,
            meetingName = "Meeting 2",
            categoryId = CategoryId.GREYHOUND,
            advertisedStart = Instant.fromEpochSeconds(1600), // 10 minutes future
        )

        val mockRepository = mockk<RaceRepository> {
            every { getNextToGoRacesStream(10) } returns flowOf(Result.Success(listOf(race2, race1))) // Unsorted
        }

        val useCase = GetNextRacesUseCase(mockRepository, mockClock)
        val result = useCase.executeStream(count = 10).first()

        assertTrue(result is Result.Success)
        val races = result.data
        assertEquals(2, races.size)
        // Should be sorted by advertised start time
        assertEquals("1", races[0].id) // Earlier race first
        assertEquals("2", races[1].id)
    }

    @Test
    fun execute_withCategoryFilter_returnsOnlyMatchingCategories() = runTest {
        val now = Instant.fromEpochSeconds(1000)
        val mockClock = mockk<Clock> {
            every { now() } returns now
        }

        val horseRace = Race(
            id = "1",
            name = "Horse Race",
            number = 1,
            meetingName = "Meeting 1",
            categoryId = CategoryId.HORSE,
            advertisedStart = Instant.fromEpochSeconds(1300),
        )
        
        val greyhoundRace = Race(
            id = "2",
            name = "Greyhound Race", 
            number = 2,
            meetingName = "Meeting 2",
            categoryId = CategoryId.GREYHOUND,
            advertisedStart = Instant.fromEpochSeconds(1600),
        )

        val mockRepository = mockk<RaceRepository> {
            every { getNextToGoRacesStream(10) } returns flowOf(Result.Success(listOf(horseRace, greyhoundRace)))
        }

        val useCase = GetNextRacesUseCase(mockRepository, mockClock)
        val result = useCase.executeStream(count = 10, categories = setOf(CategoryId.HORSE)).first()

        assertTrue(result is Result.Success)
        val races = result.data
        assertEquals(1, races.size)
        assertEquals(CategoryId.HORSE, races[0].categoryId)
    }

    @Test
    fun execute_withExpiredRaces_filtersOutOldRaces() = runTest {
        val now = Instant.fromEpochSeconds(1000)
        val mockClock = mockk<Clock> {
            every { now() } returns now
        }

        val validRace = Race(
            id = "1",
            name = "Valid Race",
            number = 1,
            meetingName = "Meeting 1", 
            categoryId = CategoryId.HORSE,
            advertisedStart = Instant.fromEpochSeconds(1300), // Future race
        )
        
        val expiredRace = Race(
            id = "2",
            name = "Expired Race",
            number = 2,
            meetingName = "Meeting 2",
            categoryId = CategoryId.GREYHOUND, 
            advertisedStart = Instant.fromEpochSeconds(880), // 2 minutes ago
        )

        val mockRepository = mockk<RaceRepository> {
            every { getNextToGoRacesStream(10) } returns flowOf(Result.Success(listOf(validRace, expiredRace)))
        }

        val useCase = GetNextRacesUseCase(mockRepository, mockClock)
        val result = useCase.executeStream(count = 10).first()

        assertTrue(result is Result.Success)
        val races = result.data
        assertEquals(1, races.size)
        assertEquals("1", races[0].id) // Only valid race remains
    }

    @Test
    fun execute_withRepositoryError_returnsError() = runTest {
        val mockClock = mockk<Clock>()
        val mockRepository = mockk<RaceRepository> {
            every { getNextToGoRacesStream(10) } returns flowOf(Result.Error(DataError.NetworkUnavailable))
        }

        val useCase = GetNextRacesUseCase(mockRepository, mockClock)
        val result = useCase.executeStream(count = 10).first()

        assertTrue(result is Result.Error)
        assertEquals(DataError.NetworkUnavailable, result.error)
    }
}