package com.yomi.next2go.core.common

import com.yomi.next2go.core.common.time.Clock
import java.time.Duration
import java.time.Instant

fun Instant.toCountdownString(clock: Clock): String {
    val now = clock.now()
    val duration = Duration.between(now, this)
    
    return when {
        duration.isNegative -> {
            val secondsPast = duration.abs().seconds
            if (secondsPast <= 60) "Starting..." else ""
        }
        else -> {
            val totalSeconds = duration.seconds
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            "${minutes}m ${seconds}s"
        }
    }
}

fun Instant.isExpired(clock: Clock, expiredThresholdSeconds: Long = 60): Boolean {
    val now = clock.now()
    val secondsPast = Duration.between(this, now).seconds
    return secondsPast > expiredThresholdSeconds
}
