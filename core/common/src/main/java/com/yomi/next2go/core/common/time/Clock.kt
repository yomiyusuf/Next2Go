package com.yomi.next2go.core.common.time

import kotlinx.datetime.Instant
import kotlinx.datetime.Clock as KotlinClock

interface Clock {
    fun now(): Instant
}

class SystemClock : Clock {
    override fun now(): Instant = KotlinClock.System.now()
}
