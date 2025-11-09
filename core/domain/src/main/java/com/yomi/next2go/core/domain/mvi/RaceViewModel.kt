package com.yomi.next2go.core.domain.mvi

import androidx.lifecycle.viewModelScope
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.CategoryColor
import com.yomi.next2go.core.domain.model.RaceDisplayModel
import com.yomi.next2go.core.domain.repository.Result
import com.yomi.next2go.core.domain.usecase.GetNextRacesUseCase
import com.yomi.next2go.core.common.time.Clock
import com.yomi.next2go.core.domain.model.Race
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.Instant
import kotlin.math.abs

class RaceViewModel(
    private val getNextRacesUseCase: GetNextRacesUseCase,
    private val clock: Clock,
) : BaseMviViewModel<RaceUiState, RaceIntent, RaceSideEffect>(
    initialState = RaceUiState()
) {

    private var currentRaces: List<Race> = emptyList()

    init {
        // Start observing races on initialization
        handleIntent(RaceIntent.LoadRaces)
        // Start countdown timer
        startCountdownTimer()
    }

    override fun handleIntent(intent: RaceIntent) {
        when (intent) {
            is RaceIntent.LoadRaces -> loadRaces()
            is RaceIntent.RefreshRaces -> refreshRaces()
            is RaceIntent.ToggleCategory -> toggleCategory(intent.category)
            is RaceIntent.ClearFilters -> clearFilters()
        }
    }

    private fun loadRaces() {
        updateState { it.copy(isLoading = true, error = null) }
        
        getNextRacesUseCase.executeStream(
            count = 10,
            categories = currentState.selectedCategories
        ).onEach { result ->
            handleRaceResult(result)
        }.launchIn(viewModelScope)
    }

    private fun refreshRaces() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            
            val result = getNextRacesUseCase.executeStream(
                count = 10,
                categories = currentState.selectedCategories
            ).first()
            
            handleRaceResult(result)
            emitSideEffect(RaceSideEffect.ShowRefreshComplete)
        }
    }

    private fun toggleCategory(category: CategoryId) {
        val currentCategories = currentState.selectedCategories
        val newCategories = if (currentCategories.contains(category)) {
            currentCategories - category
        } else {
            currentCategories + category
        }
        
        updateState { it.copy(selectedCategories = newCategories) }
        
        // Reload races with new filter
        loadRaces()
    }

    private fun clearFilters() {
        updateState { it.copy(selectedCategories = emptySet()) }
        loadRaces()
    }

    private fun handleRaceResult(result: Result<List<Race>>) {
        when (result) {
            is Result.Success -> {
                currentRaces = result.data
                val displayRaces = currentRaces.map { race ->
                    createDisplayModel(race)
                }
                updateState { 
                    it.copy(
                        isLoading = false,
                        displayRaces = displayRaces,
                        error = null
                    )
                }
            }
            is Result.Error -> {
                updateState { 
                    it.copy(
                        isLoading = false,
                        error = result.error.message ?: "Unknown error occurred"
                    )
                }
                viewModelScope.launch {
                    emitSideEffect(
                        RaceSideEffect.ShowError(
                            result.error.message ?: "Failed to load races"
                        )
                    )
                }
            }
        }
    }

    private fun startCountdownTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000) // Update every second
                updateCountdowns()
            }
        }
    }

    private fun updateCountdowns() {
        val displayRaces = currentRaces.map { race ->
            createDisplayModel(race)
        }
        updateState { it.copy(displayRaces = displayRaces) }
    }

    private fun createDisplayModel(race: Race): RaceDisplayModel {
        return RaceDisplayModel(
            id = race.id,
            raceName = "${race.meetingName} R${race.number}",
            raceNumber = race.number,
            runnerName = "Next Runner",
            runnerNumber = 1,
            jockeyName = "TBA",
            bestTime = "--:--",
            odds = "--",
            countdownText = formatCountdown(race.advertisedStart),
            categoryColor = getCategoryColor(race.categoryId),
            isLive = isRaceLive(race.advertisedStart)
        )
    }

    private fun formatCountdown(advertisedStart: Instant): String {
        val now = clock.now()
        val diffSeconds = advertisedStart.epochSecond - now.epochSecond
        
        return when {
            diffSeconds <= 0 -> "LIVE"
            diffSeconds < 60 -> "${diffSeconds}s"
            diffSeconds < 3600 -> "${diffSeconds / 60}m ${diffSeconds % 60}s"
            else -> "${diffSeconds / 3600}h ${(diffSeconds % 3600) / 60}m"
        }
    }

    private fun isRaceLive(advertisedStart: Instant): Boolean {
        val now = clock.now()
        val diffSeconds = advertisedStart.epochSecond - now.epochSecond
        return diffSeconds <= 0 && abs(diffSeconds) <= 300 // Live for 5 minutes past start
    }

    private fun getCategoryColor(categoryId: CategoryId): CategoryColor {
        return when (categoryId) {
            CategoryId.HORSE -> CategoryColor.GREEN
            CategoryId.GREYHOUND -> CategoryColor.RED
            CategoryId.HARNESS -> CategoryColor.YELLOW
        }
    }
}