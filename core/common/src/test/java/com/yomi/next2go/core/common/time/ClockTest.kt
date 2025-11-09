package com.yomi.next2go.core.common.time

import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class ClockTest {

    @Test
    fun systemClock_now_returnsCurrentTime() {
        val clock = SystemClock()
        val before = Instant.now()
        
        val result = clock.now()
        
        val after = Instant.now()
        assertTrue("Clock should return current time", 
            !result.isBefore(before) && !result.isAfter(after))
    }
}