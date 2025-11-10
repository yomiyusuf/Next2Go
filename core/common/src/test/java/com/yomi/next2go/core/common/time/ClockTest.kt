package com.yomi.next2go.core.common.time

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.datetime.Clock as KotlinClock
import kotlinx.datetime.Instant

class ClockTest {

    @Test
    fun systemClock_now_returnsCurrentTime() {
        val clock = SystemClock()
        val before = KotlinClock.System.now()
        
        val result = clock.now()
        
        val after = KotlinClock.System.now()
        assertTrue("Clock should return current time", 
            result >= before && result <= after)
    }
}