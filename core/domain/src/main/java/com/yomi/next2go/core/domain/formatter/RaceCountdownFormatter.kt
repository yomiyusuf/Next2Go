package com.yomi.next2go.core.domain.formatter

import com.yomi.next2go.core.common.time.Clock
import kotlinx.datetime.Instant
import kotlin.math.abs

object RaceCountdownFormatter {

    fun formatCountdown(advertisedStart: Instant, clock: Clock): String {
        val now = clock.now()
        val diffSeconds = advertisedStart.epochSeconds - now.epochSeconds

        return when {
            diffSeconds <= 0 -> "LIVE"
            diffSeconds < 60 -> "${diffSeconds}s"
            diffSeconds < 3600 -> "${diffSeconds / 60}m ${diffSeconds % 60}s"
            else -> "${diffSeconds / 3600}h ${(diffSeconds % 3600) / 60}m"
        }
    }

    fun isRaceLive(advertisedStart: Instant, clock: Clock): Boolean {
        val now = clock.now()
        val diffSeconds = advertisedStart.epochSeconds - now.epochSeconds
        return diffSeconds <= 0 && abs(diffSeconds) <= 300 // Live for 5 minutes past start
    }
}
