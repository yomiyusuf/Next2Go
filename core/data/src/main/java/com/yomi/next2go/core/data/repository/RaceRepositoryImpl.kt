package com.yomi.next2go.core.data.repository

import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.Race
import com.yomi.next2go.core.domain.repository.RaceRepository
import com.yomi.next2go.core.domain.repository.Result
import com.yomi.next2go.core.network.api.RacingApiService
import com.yomi.next2go.core.network.error.toDataError
import com.yomi.next2go.core.network.mapper.toDomain
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RaceRepositoryImpl(
    private val apiService: RacingApiService,
) : RaceRepository {

    override suspend fun getNextToGoRaces(
        count: Int,
        categories: Set<CategoryId>,
    ): Result<List<Race>> {
        return try {
            // Request more races initially to account for time filtering
            val requestCount = (count * 1.5).toInt().coerceAtLeast(count + 5)

            val allRaces = when {
                categories.isEmpty() -> {
                    // No categories selected - get all races
                    val response = apiService.getNextRaces(count = requestCount)
                    response.data.raceSummaries.values.mapNotNull { it.toDomain() }
                }
                categories.size == 1 -> {
                    // Single category - single API call
                    val categoryId = categories.first().id
                    val response = apiService.getNextRaces(
                        count = requestCount,
                        categoryId = categoryId,
                    )
                    response.data.raceSummaries.values.mapNotNull { it.toDomain() }
                }
                else -> {
                    // Multiple categories - parallel API calls
                    coroutineScope {
                        val perCategoryCount = (requestCount.toDouble() / categories.size).toInt().coerceAtLeast(count)
                        val raceLists = categories.map { category ->
                            async {
                                try {
                                    val response = apiService.getNextRaces(
                                        count = perCategoryCount,
                                        categoryId = category.id,
                                    )
                                    response.data.raceSummaries.values.mapNotNull { it.toDomain() }
                                } catch (e: Exception) {
                                    // If one category fails, return empty list for that category
                                    emptyList()
                                }
                            }
                        }.awaitAll()

                        // Merge all race lists, deduplicate by ID, and sort by advertised start time
                        raceLists.flatten()
                            .distinctBy { it.id }
                            .sortedBy { it.advertisedStart.epochSeconds }
                    }
                }
            }

            Result.Success(allRaces)
        } catch (exception: Exception) {
            val dataError = exception.toDataError()
            Result.Error(dataError)
        }
    }

    override fun getNextToGoRacesStream(
        count: Int,
        categories: Set<CategoryId>,
    ): Flow<Result<List<Race>>> = flow {
        // Single emission - auto-refresh is handled at ViewModel level
        emit(getNextToGoRaces(count, categories))
    }
}
