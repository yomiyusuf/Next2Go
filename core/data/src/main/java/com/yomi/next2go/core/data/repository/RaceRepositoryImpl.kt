package com.yomi.next2go.core.data.repository

import com.yomi.next2go.core.domain.model.Race
import com.yomi.next2go.core.domain.repository.RaceRepository
import com.yomi.next2go.core.domain.repository.Result
import com.yomi.next2go.core.network.api.RacingApiService
import com.yomi.next2go.core.network.error.toDataError
import com.yomi.next2go.core.network.mapper.toDomain
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RaceRepositoryImpl(
    private val apiService: RacingApiService,
) : RaceRepository {

    override suspend fun getNextToGoRaces(count: Int): Result<List<Race>> {
        return try {
            val response = apiService.getNextRaces(count = count)
            val races = response.data.raceSummaries.values.mapNotNull { raceDto ->
                raceDto.toDomain()
            }
            Result.Success(races)
        } catch (exception: Exception) {
            val dataError = exception.toDataError()
            Result.Error(dataError)
        }
    }

    override fun getNextToGoRacesStream(count: Int): Flow<Result<List<Race>>> = flow {
        while (true) {
            emit(getNextToGoRaces(count))
            delay(30_000) // Refresh race data every 30 seconds
        }
    }
}
