package com.yomi.next2go.core.domain.mvi

import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.Race
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class RaceContractTest {

    @Test
    fun raceUiState_defaultValues_areCorrect() {
        val state = RaceUiState()
        
        assertFalse(state.isLoading)
        assertTrue(state.races.isEmpty())
        assertTrue(state.selectedCategories.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun raceUiState_withCustomValues_retainsValues() {
        val races = listOf(
            Race(
                id = "1",
                name = "Test Race",
                number = 1,
                meetingName = "Test Meeting",
                categoryId = CategoryId.HORSE,
                advertisedStart = Instant.now()
            )
        )
        val selectedCategories = setOf(CategoryId.HORSE, CategoryId.GREYHOUND)
        
        val state = RaceUiState(
            isLoading = true,
            races = races,
            selectedCategories = selectedCategories,
            error = "Test error"
        )
        
        assertTrue(state.isLoading)
        assertEquals(1, state.races.size)
        assertEquals("Test Race", state.races.first().name)
        assertEquals(2, state.selectedCategories.size)
        assertTrue(state.selectedCategories.contains(CategoryId.HORSE))
        assertTrue(state.selectedCategories.contains(CategoryId.GREYHOUND))
        assertEquals("Test error", state.error)
    }

    @Test
    fun raceUiState_copy_preservesUnchangedValues() {
        val originalState = RaceUiState(
            isLoading = false,
            races = emptyList(),
            selectedCategories = setOf(CategoryId.HORSE),
            error = null
        )
        
        val copiedState = originalState.copy(isLoading = true)
        
        assertTrue(copiedState.isLoading) // Changed
        assertTrue(copiedState.races.isEmpty()) // Preserved
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