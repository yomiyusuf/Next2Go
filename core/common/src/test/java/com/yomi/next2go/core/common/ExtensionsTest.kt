package com.yomi.next2go.core.common

import com.yomi.next2go.core.common.time.Clock
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExtensionsTest {

    private val testClock = TestClock()

    @Test
    fun toCountdownString_futureTime_returnsMinutesAndSeconds() {
        val now = Instant.fromEpochSeconds(1000)
        val future = Instant.fromEpochSeconds(1153) // 153s = 2m 33s in future
        testClock.setNow(now)

        val result = future.toCountdownString(testClock)

        assertEquals("2m 33s", result)
    }

    @Test
    fun toCountdownString_exactlyOneMinute_returnsOneMinuteZeroSeconds() {
        val now = Instant.fromEpochSeconds(1000)
        val future = Instant.fromEpochSeconds(1060) // 60s = 1m in future
        testClock.setNow(now)

        val result = future.toCountdownString(testClock)

        assertEquals("1m 0s", result)
    }

    @Test
    fun toCountdownString_withinStartingThreshold_returnsStarting() {
        val now = Instant.fromEpochSeconds(1000)
        val past = Instant.fromEpochSeconds(970) // 30 seconds past
        testClock.setNow(now)

        val result = past.toCountdownString(testClock)

        assertEquals("Starting...", result)
    }

    @Test
    fun toCountdownString_pastStartingThreshold_returnsEmpty() {
        val now = Instant.fromEpochSeconds(1000)
        val past = Instant.fromEpochSeconds(910) // 90 seconds past
        testClock.setNow(now)

        val result = past.toCountdownString(testClock)

        assertEquals("", result)
    }

    @Test
    fun isExpired_withinThreshold_returnsFalse() {
        val now = Instant.fromEpochSeconds(1000)
        val raceStart = Instant.fromEpochSeconds(970) // 30 seconds past
        testClock.setNow(now)

        val result = raceStart.isExpired(testClock)

        assertFalse(result)
    }

    @Test
    fun isExpired_pastThreshold_returnsTrue() {
        val now = Instant.fromEpochSeconds(1000)
        val raceStart = Instant.fromEpochSeconds(910) // 90 seconds past
        testClock.setNow(now)

        val result = raceStart.isExpired(testClock)

        assertTrue(result)
    }

    @Test
    fun isExpired_customThreshold_respectsCustomValue() {
        val now = Instant.fromEpochSeconds(1000)
        val raceStart = Instant.fromEpochSeconds(880) // 120 seconds past
        testClock.setNow(now)

        val result = raceStart.isExpired(testClock, expiredThresholdSeconds = 180)

        assertFalse(result) // 120 < 180, so not expired
    }

    private class TestClock : Clock {
        private var currentTime = Instant.fromEpochSeconds(0)

        fun setNow(instant: Instant) {
            currentTime = instant
        }

        override fun now(): Instant = currentTime
    }
}
