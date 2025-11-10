package com.yomi.next2go.core.domain.mapper

import com.yomi.next2go.core.common.time.Clock
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.CategoryColor
import com.yomi.next2go.core.domain.model.Race
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RaceDisplayModelMapperTest {

    private lateinit var mapper: RaceDisplayModelMapper
    private lateinit var mockClock: MockClock

    @BeforeTest
    fun setup() {
        mockClock = MockClock()
        mapper = RaceDisplayModelMapper(mockClock)
    }

    @Test
    fun `mapToDisplayModel correctly maps horse race`() {
        val race = Race(
            id = "race123",
            name = "Test Race",
            number = 8,
            meetingName = "BATHURST",
            categoryId = CategoryId.HORSE,
            advertisedStart = Instant.fromEpochSeconds(1150) // 150 seconds in future
        )
        mockClock.setNow(Instant.fromEpochSeconds(1000))

        val result = mapper.mapToDisplayModel(race)

        assertEquals("race123", result.id)
        assertEquals("BATHURST R8", result.raceName)
        assertEquals(8, result.raceNumber)
        assertEquals("Next Runner", result.runnerName)
        assertEquals(1, result.runnerNumber)
        assertEquals("TBA", result.jockeyName)
        assertEquals("--:--", result.bestTime)
        assertEquals("--", result.odds)
        assertEquals("2m 30s", result.countdownText) // 150s = 2m 30s
        assertEquals(CategoryColor.GREEN, result.categoryColor)
        assertEquals(false, result.isLive)
    }

    @Test
    fun `mapToDisplayModel correctly maps greyhound race`() {
        val race = Race(
            id = "race456",
            name = "Greyhound Sprint",
            number = 3,
            meetingName = "CANNINGTON",
            categoryId = CategoryId.GREYHOUND,
            advertisedStart = Instant.fromEpochSeconds(999) // 1 second ago (LIVE)
        )
        mockClock.setNow(Instant.fromEpochSeconds(1000))

        val result = mapper.mapToDisplayModel(race)

        assertEquals("CANNINGTON R3", result.raceName)
        assertEquals(3, result.raceNumber)
        assertEquals("LIVE", result.countdownText)
        assertEquals(CategoryColor.RED, result.categoryColor)
        assertEquals(true, result.isLive)
    }

    @Test
    fun `mapToDisplayModel correctly maps harness race`() {
        val race = Race(
            id = "race789",
            name = "Harness Classic",
            number = 5,
            meetingName = "MENANGLE",
            categoryId = CategoryId.HARNESS,
            advertisedStart = Instant.fromEpochSeconds(5500) // 4500s = 1h 15m from now
        )
        mockClock.setNow(Instant.fromEpochSeconds(1000))

        val result = mapper.mapToDisplayModel(race)

        assertEquals("MENANGLE R5", result.raceName)
        assertEquals("1h 15m", result.countdownText)
        assertEquals(CategoryColor.YELLOW, result.categoryColor)
    }

    private class MockClock : Clock {
        private var currentTime = Instant.fromEpochSeconds(0)

        fun setNow(time: Instant) {
            currentTime = time
        }

        override fun now(): Instant = currentTime
    }
}