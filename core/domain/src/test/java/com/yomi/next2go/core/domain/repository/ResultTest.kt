package com.yomi.next2go.core.domain.repository

import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.DataError
import com.yomi.next2go.core.domain.model.Race
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class ResultTest {
    @Test
    fun result_success_containsData() {
        val race = Race(
            id = "test-id",
            name = "Test Race",
            number = 1,
            meetingName = "Test Meeting",
            categoryId = CategoryId.HORSE,
            advertisedStart = Instant.fromEpochSeconds(1000),
        )
        val result = Result.Success(listOf(race))

        assertEquals(1, result.data.size)
        assertEquals("test-id", result.data.first().id)
    }

    @Test
    fun result_error_containsDataError() {
        val error = DataError.Timeout
        val result = Result.Error(error)

        assertEquals(error, result.error)
    }
}
