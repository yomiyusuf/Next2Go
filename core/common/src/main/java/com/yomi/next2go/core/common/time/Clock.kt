package com.yomi.next2go.core.common.time

import kotlinx.datetime.Clock as KotlinClock
import kotlinx.datetime.Instant

interface Clock {
    fun now(): Instant
}

class SystemClock : Clock {
    override fun now(): Instant = KotlinClock.System.now()
}