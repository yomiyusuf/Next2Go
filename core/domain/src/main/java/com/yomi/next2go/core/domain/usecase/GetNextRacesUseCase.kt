package com.yomi.next2go.core.domain.usecase

import com.yomi.next2go.core.common.time.Clock
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.Race
import com.yomi.next2go.core.domain.repository.RaceRepository
import com.yomi.next2go.core.domain.repository.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration

class GetNextRacesUseCase(
    private val repository: RaceRepository,
    private val clock: Clock,
) {
    fun executeStream(
        count: Int = 10,
        categories: Set<CategoryId> = emptySet(),
    ): Flow<Result<List<Race>>> {
        return repository.getNextToGoRacesStream(count).map { result ->
            when (result) {
                is Result.Success -> {
                    val filteredRaces = filterAndSortRaces(result.data, categories, count)
                    Result.Success(filteredRaces)
                }
                is Result.Error -> result
            }
        }
    }

    private fun filterAndSortRaces(
        races: List<Race>,
        categories: Set<CategoryId>,
        count: Int
    ): List<Race> {
        return races
            .filter { race -> isRaceValid(race) }
            .filter { race -> categories.isEmpty() || race.categoryId in categories }
            .sortedBy { it.advertisedStart }
            .take(count)
    }

    private fun isRaceValid(race: Race): Boolean {
        val now = clock.now()
        val timeSinceStart = Duration.between(race.advertisedStart, now)
        
        // Remove races that started more than 1 minute ago
        return timeSinceStart.toMinutes() < 1
    }
}