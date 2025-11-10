package com.yomi.next2go.core.domain.timer

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CountdownTimerTest {

    @Test
    fun `timer starts and calls onTick after interval`() = runTest {
        var tickCount = 0
        val timer = CountdownTimerImpl(
            scope = this,
            intervalMs = 1000L
        )

        timer.start { tickCount++ }
        assertTrue(timer.isRunning)

        // Advance time by 1 second
        advanceTimeBy(1000L)
        runCurrent() // Ensure coroutines run to completion
        assertEquals(1, tickCount)

        // Advance time by another 2 seconds
        advanceTimeBy(2000L)
        runCurrent() // Ensure coroutines run to completion
        assertEquals(3, tickCount)

        timer.stop()
    }

    @Test
    fun `timer can be stopped`() = runTest {
        var tickCount = 0
        val timer = CountdownTimerImpl(
            scope = this,
            intervalMs = 1000L
        )

        timer.start { tickCount++ }
        assertTrue(timer.isRunning)

        advanceTimeBy(1000L)
        runCurrent() // Ensure coroutines run to completion
        assertEquals(1, tickCount)

        timer.stop()
        assertFalse(timer.isRunning)

        // Advance time after stopping
        advanceTimeBy(2000L)
        runCurrent() // Ensure coroutines run to completion
        assertEquals(1, tickCount) // Should not increase
    }

    @Test
    fun `debug simple timer test`() = runTest {
        var tickCount = 0
        
        // Create a simple coroutine that mimics timer behavior
        launch {
            delay(1000L)
            tickCount++
        }
        
        advanceTimeBy(1000L)
        runCurrent() // Ensure coroutines run to completion
        assertEquals(1, tickCount,)
    }

    @Test
    fun `starting timer again stops previous timer`() = runTest {
        var firstTimerTicks = 0
        var secondTimerTicks = 0
        val timer = CountdownTimerImpl(
            scope = this,
            intervalMs = 1000L
        )

        // Start first timer
        timer.start { firstTimerTicks++ }
        assertTrue(timer.isRunning)
        
        advanceTimeBy(1000L)
        runCurrent() // Ensure coroutines run to completion
        assertEquals(1, firstTimerTicks)

        // Start timer again (should stop the previous one and start a new one)
        timer.start { secondTimerTicks++ }
        assertTrue(timer.isRunning)
        
        // Advance time to let second timer tick
        advanceTimeBy(1000L)
        runCurrent() // Ensure coroutines run to completion
        
        // First timer should not have ticked again (it was stopped)
        assertEquals(1, firstTimerTicks)
        // Second timer should have ticked once
        assertEquals(1, secondTimerTicks)

        timer.stop()
    }

    @Test
    fun `timer respects custom interval`() = runTest {
        var tickCount = 0
        val timer = CountdownTimerImpl(
            scope = this,
            intervalMs = 500L // Custom 500ms interval
        )

        timer.start { tickCount++ }

        // Advance by 500ms - should tick once
        advanceTimeBy(500L)
        runCurrent() // Ensure coroutines run to completion
        assertEquals(1, tickCount)

        // Advance by another 500ms - should tick again
        advanceTimeBy(500L)
        runCurrent() // Ensure coroutines run to completion
        assertEquals(2, tickCount)

        // Advance by 1000ms more - should tick twice more
        advanceTimeBy(1000L)
        runCurrent() // Ensure coroutines run to completion
        assertEquals(4, tickCount)

        timer.stop()
    }
}