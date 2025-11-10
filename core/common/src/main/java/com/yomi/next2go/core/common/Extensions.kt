package com.yomi.next2go.core.common

import com.yomi.next2go.core.common.time.Clock
import kotlinx.datetime.Instant
import kotlin.math.abs

fun Instant.toCountdownString(clock: Clock): String {
    val now = clock.now()
    val diffSeconds = this.epochSeconds - now.epochSeconds
    
    return when {
        diffSeconds < 0 -> {
            val secondsPast = abs(diffSeconds)
            if (secondsPast <= 60) "Starting..." else ""
        }
        else -> {
            val minutes = diffSeconds / 60
            val seconds = diffSeconds % 60
            "${minutes}m ${seconds}s"
        }
    }
}

fun Instant.isExpired(clock: Clock, expiredThresholdSeconds: Long = 60): Boolean {
    val now = clock.now()
    val secondsPast = now.epochSeconds - this.epochSeconds
    return secondsPast > expiredThresholdSeconds
}
