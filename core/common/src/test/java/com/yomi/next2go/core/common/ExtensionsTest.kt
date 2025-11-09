package com.yomi.next2go.core.common

import com.yomi.next2go.core.common.time.Clock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class ExtensionsTest {

    private val testClock = TestClock()

    @Test
    fun toCountdownString_futureTime_returnsMinutesAndSeconds() {
        val now = Instant.parse("2024-01-01T12:00:00Z")
        val future = Instant.parse("2024-01-01T12:02:33Z") // 2m 33s in future
        testClock.setNow(now)

        val result = future.toCountdownString(testClock)

        assertEquals("2m 33s", result)
    }

    @Test
    fun toCountdownString_exactlyOneMinute_returnsOneMinuteZeroSeconds() {
        val now = Instant.parse("2024-01-01T12:00:00Z")
        val future = Instant.parse("2024-01-01T12:01:00Z")
        testClock.setNow(now)

        val result = future.toCountdownString(testClock)

        assertEquals("1m 0s", result)
    }

    @Test
    fun toCountdownString_withinStartingThreshold_returnsStarting() {
        val now = Instant.parse("2024-01-01T12:00:00Z")
        val past = Instant.parse("2024-01-01T11:59:30Z") // 30 seconds past
        testClock.setNow(now)

        val result = past.toCountdownString(testClock)

        assertEquals("Starting...", result)
    }

    @Test
    fun toCountdownString_pastStartingThreshold_returnsEmpty() {
        val now = Instant.parse("2024-01-01T12:00:00Z")
        val past = Instant.parse("2024-01-01T11:58:30Z") // 90 seconds past
        testClock.setNow(now)

        val result = past.toCountdownString(testClock)

        assertEquals("", result)
    }

    @Test
    fun isExpired_withinThreshold_returnsFalse() {
        val now = Instant.parse("2024-01-01T12:00:00Z")
        val raceStart = Instant.parse("2024-01-01T11:59:30Z") // 30 seconds past
        testClock.setNow(now)

        val result = raceStart.isExpired(testClock)

        assertFalse(result)
    }

    @Test
    fun isExpired_pastThreshold_returnsTrue() {
        val now = Instant.parse("2024-01-01T12:00:00Z")
        val raceStart = Instant.parse("2024-01-01T11:58:30Z") // 90 seconds past
        testClock.setNow(now)

        val result = raceStart.isExpired(testClock)

        assertTrue(result)
    }

    @Test
    fun isExpired_customThreshold_respectsCustomValue() {
        val now = Instant.parse("2024-01-01T12:00:00Z")
        val raceStart = Instant.parse("2024-01-01T11:58:00Z") // 120 seconds past
        testClock.setNow(now)

        val result = raceStart.isExpired(testClock, expiredThresholdSeconds = 180)

        assertFalse(result) // 120 < 180, so not expired
    }

    private class TestClock : Clock {
        private var currentTime = Instant.now()

        fun setNow(instant: Instant) {
            currentTime = instant
        }

        override fun now(): Instant = currentTime
    }
}
