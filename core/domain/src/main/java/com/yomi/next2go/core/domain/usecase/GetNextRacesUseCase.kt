package com.yomi.next2go.core.domain.usecase

import com.yomi.next2go.core.common.time.Clock
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.Race
import com.yomi.next2go.core.domain.repository.RaceRepository
import com.yomi.next2go.core.domain.repository.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetNextRacesUseCase(
    private val repository: RaceRepository,
    private val clock: Clock,
) {
    fun executeStream(
        count: Int = 10,
        categories: Set<CategoryId> = emptySet(),
    ): Flow<Result<List<Race>>> {
        return repository.getNextToGoRacesStream(count, categories).map { result ->
            when (result) {
                is Result.Success -> {
                    val filteredRaces = result.data
                        .let { races -> filterByTime(races) }
                        .let { races -> filterByCategories(races, categories) }
                        .sortedBy { it.advertisedStart.epochSeconds }
                        .take(count)
                    Result.Success(filteredRaces)
                }
                is Result.Error -> result
            }
        }
    }

    private fun filterByTime(races: List<Race>): List<Race> {
        return races.filter { race -> isRaceValid(race) }
    }

    private fun filterByCategories(races: List<Race>, categories: Set<CategoryId>): List<Race> {
        // If no categories are selected, return all races
        if (categories.isEmpty()) {
            return races
        }
        // Filter races to only include selected categories
        return races.filter { race -> categories.contains(race.categoryId) }
    }

    private fun isRaceValid(race: Race): Boolean {
        val now = clock.now()
        val secondsSinceStart = now.epochSeconds - race.advertisedStart.epochSeconds

        // Remove races that started more than 1 minute ago
        return secondsSinceStart < 60
    }
}
