package com.yomi.next2go.core.domain.mvi

import com.yomi.next2go.core.domain.model.CategoryColor
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.RaceDisplayModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RaceContractTest {

    @Test
    fun raceUiState_defaultValues_areCorrect() {
        val state = RaceUiState()

        assertFalse(state.isLoading)
        assertTrue(state.displayRaces.isEmpty())
        assertTrue(state.selectedCategories.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun raceUiState_withCustomValues_retainsValues() {
        val displayRaces = listOf(
            RaceDisplayModel(
                id = "1",
                raceName = "Test Race",
                raceNumber = 1,
                runnerName = "Test Runner",
                runnerNumber = 1,
                jockeyName = "Test Jockey",
                bestTime = "30.00",
                odds = "2.50",
                countdownText = "5m 30s",
                categoryColor = CategoryColor.GREEN,
                isLive = false,
            ),
        )
        val selectedCategories = setOf(CategoryId.HORSE, CategoryId.GREYHOUND)

        val state = RaceUiState(
            isLoading = true,
            displayRaces = displayRaces,
            selectedCategories = selectedCategories,
            error = "Test error",
        )

        assertTrue(state.isLoading)
        assertEquals(1, state.displayRaces.size)
        assertEquals("Test Race", state.displayRaces.first().raceName)
        assertEquals(2, state.selectedCategories.size)
        assertTrue(state.selectedCategories.contains(CategoryId.HORSE))
        assertTrue(state.selectedCategories.contains(CategoryId.GREYHOUND))
        assertEquals("Test error", state.error)
    }

    @Test
    fun raceUiState_copy_preservesUnchangedValues() {
        val originalState = RaceUiState(
            isLoading = false,
            displayRaces = emptyList(),
            selectedCategories = setOf(CategoryId.HORSE),
            error = null,
        )

        val copiedState = originalState.copy(isLoading = true)

        assertTrue(copiedState.isLoading) // Changed
        assertTrue(copiedState.displayRaces.isEmpty()) // Preserved
        assertTrue(copiedState.selectedCategories.contains(CategoryId.HORSE)) // Preserved
        assertNull(copiedState.error) // Preserved
    }

    @Test
    fun raceIntent_toggleCategory_hasCorrectCategory() {
        val intent = RaceIntent.ToggleCategory(CategoryId.HARNESS)

        assertTrue(intent is RaceIntent.ToggleCategory)
        assertEquals(CategoryId.HARNESS, (intent as RaceIntent.ToggleCategory).category)
    }

    @Test
    fun raceSideEffect_showError_hasCorrectMessage() {
        val message = "Network error occurred"
        val sideEffect = RaceSideEffect.ShowError(message)

        assertTrue(sideEffect is RaceSideEffect.ShowError)
        assertEquals(message, (sideEffect as RaceSideEffect.ShowError).message)
    }

    @Test
    fun raceIntent_objectIntents_areCorrectType() {
        assertTrue(RaceIntent.LoadRaces is RaceIntent.LoadRaces)
        assertTrue(RaceIntent.RefreshRaces is RaceIntent.RefreshRaces)
        assertTrue(RaceIntent.ClearFilters is RaceIntent.ClearFilters)
    }

    @Test
    fun raceSideEffect_objectSideEffects_areCorrectType() {
        assertTrue(RaceSideEffect.ShowRefreshComplete is RaceSideEffect.ShowRefreshComplete)
    }
}
