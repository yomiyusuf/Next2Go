package com.yomi.next2go.core.domain.model

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class RaceTest {
    @Test
    fun race_withApiResponseData_createsCorrectRace() {
        val raceId = "22e36327-6980-4836-8d8d-f1da0362e9b5"
        val raceName = "Burwood Stud 2Yo 0 To 1 Win Mobile Pace"
        val raceNumber = 8
        val meetingName = "Redcliffe"
        val categoryId = CategoryId.HARNESS
        val advertisedStart = Instant.fromEpochSeconds(1762340700)

        val race = Race(
            id = raceId,
            name = raceName,
            number = raceNumber,
            meetingName = meetingName,
            categoryId = categoryId,
            advertisedStart = advertisedStart,
        )

        assertEquals(raceId, race.id)
        assertEquals(raceName, race.name)
        assertEquals(raceNumber, race.number)
        assertEquals(meetingName, race.meetingName)
        assertEquals(categoryId, race.categoryId)
        assertEquals(advertisedStart, race.advertisedStart)
    }
}
