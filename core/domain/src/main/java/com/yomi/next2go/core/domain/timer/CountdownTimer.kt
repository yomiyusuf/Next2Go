package com.yomi.next2go.core.domain.timer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

interface CountdownTimer {
    fun start(onTick: () -> Unit)
    fun stop()
    val isRunning: Boolean
}

class CountdownTimerImpl(
    private val scope: CoroutineScope,
    private val intervalMs: Long = 1000L
) : CountdownTimer {
    private var timerJob: Job? = null

    override fun start(onTick: () -> Unit) {
        stop() // Ensure any existing timer is stopped
        timerJob = scope.launch {
            while (isActive) {
                delay(intervalMs)
                if (isActive) {
                    onTick()
                }
            }
        }
    }

    override fun stop() {
        timerJob?.cancel()
        timerJob = null
    }

    override val isRunning: Boolean
        get() = timerJob?.isActive == true
}