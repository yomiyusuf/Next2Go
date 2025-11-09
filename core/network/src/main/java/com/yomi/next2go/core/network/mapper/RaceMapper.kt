package com.yomi.next2go.core.network.mapper

import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.Race
import com.yomi.next2go.core.network.dto.RaceDto
import java.time.Instant

fun RaceDto.toDomain(): Race? {
    val categoryId = CategoryId.fromId(this.categoryId) ?: return null
    
    return Race(
        id = this.raceId,
        name = this.raceName,
        number = this.raceNumber,
        meetingName = this.meetingName,
        categoryId = categoryId,
        advertisedStart = Instant.ofEpochSecond(this.advertisedStart.seconds),
    )
}