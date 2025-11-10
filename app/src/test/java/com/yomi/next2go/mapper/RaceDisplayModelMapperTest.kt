package com.yomi.next2go.mapper

import android.content.Context
import com.yomi.next2go.R
import com.yomi.next2go.core.common.time.Clock
import com.yomi.next2go.core.domain.model.CategoryColor
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.Race
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RaceDisplayModelMapperTest {

    private val mockContext = mockk<Context>()
    private val mockClock = mockk<Clock>()
    private lateinit var mapper: RaceDisplayModelMapper

    @BeforeTest
    fun setup() {
        every { mockContext.getString(R.string.category_horse_racing) } returns "Horse Racing"
        every { mockContext.getString(R.string.category_greyhound_racing) } returns "Greyhound Racing"
        every { mockContext.getString(R.string.category_harness_racing) } returns "Harness Racing"

        mapper = RaceDisplayModelMapper(mockClock, mockContext)
    }

    @Test
    fun `mapToDisplayModel correctly maps horse race with content description`() {
        every { mockClock.now() } returns Instant.fromEpochSeconds(1000)

        // 150 seconds in future
        val race = Race(
            id = "race123",
            name = "Test Race",
            number = 8,
            meetingName = "BATHURST",
            categoryId = CategoryId.HORSE,
            advertisedStart = Instant.fromEpochSeconds(1150),
        )

        val result = mapper.mapToDisplayModel(race)

        assertEquals("race123", result.id)
        assertEquals("BATHURST", result.raceName)
        assertEquals(8, result.raceNumber)
        assertEquals("Next Runner", result.runnerName)
        assertEquals(1, result.runnerNumber)
        assertEquals("2m 30s", result.countdownText) // 150s = 2m 30s
        assertEquals(CategoryColor.GREEN, result.categoryColor)
        assertEquals(CategoryId.HORSE, result.categoryId)
        assertEquals(false, result.isLive)
        assertTrue(result.contentDescription.contains("Horse Racing race"))
        assertTrue(result.contentDescription.contains("number 8"))
        assertTrue(result.contentDescription.contains("BATHURST"))
        assertTrue(result.contentDescription.contains("Starting in 2m 30s"))
    }

    @Test
    fun `mapToDisplayModel correctly maps live race with proper content description`() {
        every { mockClock.now() } returns Instant.fromEpochSeconds(1000)

        // 1 second ago (LIVE)
        val race = Race(
            id = "race456",
            name = "Greyhound Sprint",
            number = 3,
            meetingName = "CANNINGTON",
            categoryId = CategoryId.GREYHOUND,
            advertisedStart = Instant.fromEpochSeconds(999),
        )

        val result = mapper.mapToDisplayModel(race)

        assertEquals("CANNINGTON", result.raceName)
        assertEquals(3, result.raceNumber)
        assertEquals("LIVE", result.countdownText)
        assertEquals(CategoryColor.RED, result.categoryColor)
        assertEquals(true, result.isLive)
        assertTrue(result.contentDescription.contains("Greyhound Racing race"))
        assertTrue(result.contentDescription.contains("Race is currently live"))
    }
}
