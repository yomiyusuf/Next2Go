package com.yomi.next2go.core.common.time

import java.time.Instant

interface Clock {
    fun now(): Instant
}

class SystemClock : Clock {
    override fun now(): Instant = Instant.now()
}