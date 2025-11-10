package com.yomi.next2go.core.domain.repository

import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.DataError
import com.yomi.next2go.core.domain.model.Race
import kotlinx.coroutines.flow.Flow

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val error: DataError) : Result<Nothing>
}

interface RaceRepository {
    suspend fun getNextToGoRaces(
        count: Int = 10,
        categories: Set<CategoryId> = emptySet(),
    ): Result<List<Race>>

    fun getNextToGoRacesStream(
        count: Int = 10,
        categories: Set<CategoryId> = emptySet(),
    ): Flow<Result<List<Race>>>
}
