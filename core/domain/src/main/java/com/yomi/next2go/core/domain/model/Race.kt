package com.yomi.next2go.core.domain.model

import java.time.Instant

data class Race(
    val id: String,
    val name: String,
    val number: Int,
    val meetingName: String,
    val categoryId: CategoryId,
    val advertisedStart: Instant,
)
