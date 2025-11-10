package com.yomi.next2go.core.domain.mvi

import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.RaceDisplayModel

data class RaceUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val displayRaces: List<RaceDisplayModel> = emptyList(),
    val selectedCategories: Set<CategoryId> = emptySet(),
    val error: String? = null,
) : UiState

sealed interface RaceIntent : UiIntent {
    data object LoadRaces : RaceIntent
    data object RefreshRaces : RaceIntent
    data class ToggleCategory(val category: CategoryId) : RaceIntent
    data object ClearFilters : RaceIntent
}

sealed interface RaceSideEffect : UiSideEffect {
    data class ShowError(val message: String) : RaceSideEffect
    data object ShowRefreshComplete : RaceSideEffect
}
