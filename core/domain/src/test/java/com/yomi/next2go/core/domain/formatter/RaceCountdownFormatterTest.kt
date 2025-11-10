package com.yomi.next2go.core.domain.formatter

import com.yomi.next2go.core.common.time.Clock
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RaceCountdownFormatterTest {

    private lateinit var mockClock: TestClock

    @BeforeTest
    fun setup() {
        mockClock = TestClock()
    }

    @Test
    fun `formatCountdown returns LIVE when race has started`() {
        val now = Instant.fromEpochSeconds(1000)
        val raceStart = Instant.fromEpochSeconds(999) // 1 second ago
        mockClock.setNow(now)

        val result = RaceCountdownFormatter.formatCountdown(raceStart, mockClock)

        assertEquals("LIVE", result)
    }

    @Test
    fun `formatCountdown returns seconds when less than 1 minute remaining`() {
        val now = Instant.fromEpochSeconds(1000)
        val raceStart = Instant.fromEpochSeconds(1030) // 30 seconds from now
        mockClock.setNow(now)

        val result = RaceCountdownFormatter.formatCountdown(raceStart, mockClock)

        assertEquals("30s", result)
    }

    @Test
    fun `formatCountdown returns minutes and seconds when less than 1 hour remaining`() {
        val now = Instant.fromEpochSeconds(1000)
        val raceStart = Instant.fromEpochSeconds(1000 + 125) // 2 minutes 5 seconds from now
        mockClock.setNow(now)

        val result = RaceCountdownFormatter.formatCountdown(raceStart, mockClock)

        assertEquals("2m 5s", result)
    }

    @Test
    fun `formatCountdown returns hours and minutes when more than 1 hour remaining`() {
        val now = Instant.fromEpochSeconds(1000)
        val raceStart = Instant.fromEpochSeconds(1000 + 3780) // 1 hour 3 minutes from now
        mockClock.setNow(now)

        val result = RaceCountdownFormatter.formatCountdown(raceStart, mockClock)

        assertEquals("1h 3m", result)
    }

    @Test
    fun `isRaceLive returns true when race just started`() {
        val now = Instant.fromEpochSeconds(1000)
        val raceStart = Instant.fromEpochSeconds(999) // 1 second ago
        mockClock.setNow(now)

        val result = RaceCountdownFormatter.isRaceLive(raceStart, mockClock)

        assertTrue(result)
    }

    @Test
    fun `isRaceLive returns true when race started 5 minutes ago`() {
        val now = Instant.fromEpochSeconds(1000)
        val raceStart = Instant.fromEpochSeconds(1000 - 300) // 5 minutes ago
        mockClock.setNow(now)

        val result = RaceCountdownFormatter.isRaceLive(raceStart, mockClock)

        assertTrue(result)
    }

    @Test
    fun `isRaceLive returns false when race started more than 5 minutes ago`() {
        val now = Instant.fromEpochSeconds(1000)
        val raceStart = Instant.fromEpochSeconds(1000 - 301) // 5 minutes 1 second ago
        mockClock.setNow(now)

        val result = RaceCountdownFormatter.isRaceLive(raceStart, mockClock)

        assertFalse(result)
    }

    @Test
    fun `isRaceLive returns false when race hasn't started yet`() {
        val now = Instant.fromEpochSeconds(1000)
        val raceStart = Instant.fromEpochSeconds(1001) // 1 second from now
        mockClock.setNow(now)

        val result = RaceCountdownFormatter.isRaceLive(raceStart, mockClock)

        assertFalse(result)
    }

    private class TestClock : Clock {
        private var currentTime = Instant.fromEpochSeconds(0)

        fun setNow(time: Instant) {
            currentTime = time
        }

        override fun now(): Instant = currentTime
    }
}
