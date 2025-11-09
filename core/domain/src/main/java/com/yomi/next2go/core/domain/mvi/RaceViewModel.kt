package com.yomi.next2go.core.domain.mvi

import androidx.lifecycle.viewModelScope
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.repository.Result
import com.yomi.next2go.core.domain.usecase.GetNextRacesUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RaceViewModel(
    private val getNextRacesUseCase: GetNextRacesUseCase,
) : BaseMviViewModel<RaceUiState, RaceIntent, RaceSideEffect>(
    initialState = RaceUiState()
) {

    init {
        // Start observing races on initialization
        handleIntent(RaceIntent.LoadRaces)
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
            
            val result = getNextRacesUseCase.execute(
                count = 10,
                categories = currentState.selectedCategories
            )
            
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

    private fun handleRaceResult(result: Result<List<com.yomi.next2go.core.domain.model.Race>>) {
        when (result) {
            is Result.Success -> {
                updateState { 
                    it.copy(
                        isLoading = false,
                        races = result.data,
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
}