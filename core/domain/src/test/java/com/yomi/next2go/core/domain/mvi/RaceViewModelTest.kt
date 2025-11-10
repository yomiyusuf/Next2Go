package com.yomi.next2go.core.domain.mvi

import app.cash.turbine.test
import com.yomi.next2go.core.domain.mapper.RaceDisplayModelMapper
import com.yomi.next2go.core.domain.model.CategoryColor
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.DataError
import com.yomi.next2go.core.domain.model.Race
import com.yomi.next2go.core.domain.model.RaceDisplayModel
import com.yomi.next2go.core.domain.repository.Result
import com.yomi.next2go.core.domain.timer.CountdownTimer
import com.yomi.next2go.core.domain.usecase.GetNextRacesUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RaceViewModelTest {
    private val mockUseCase = mockk<GetNextRacesUseCase>()
    private val mockDisplayModelMapper = mockk<RaceDisplayModelMapper>()
    private val mockCountdownTimer = mockk<CountdownTimer>(relaxed = true)

    private val sampleDisplayRaces = listOf(
        RaceDisplayModel(
            id = "1",
            raceName = "Meeting 1 R1",
            raceNumber = 1,
            runnerName = "Next Runner",
            runnerNumber = 1,
            countdownText = "5m 0s",
            categoryColor = CategoryColor.GREEN,
            categoryId = CategoryId.HORSE,
            isLive = false,
        ),
        RaceDisplayModel(
            id = "2",
            raceName = "Meeting 2 R2",
            raceNumber = 2,
            runnerName = "Next Runner",
            runnerNumber = 1,
            countdownText = "10m 0s",
            categoryColor = CategoryColor.RED,
            categoryId = CategoryId.GREYHOUND,
            isLive = false,
        ),
    )

    private val sampleRaces = listOf(
        Race(
            id = "1",
            name = "Horse Race 1",
            number = 1,
            meetingName = "Meeting 1",
            categoryId = CategoryId.HORSE,
            advertisedStart = Instant.fromEpochSeconds(1000),
        ),
        Race(
            id = "2",
            name = "Greyhound Race 1",
            number = 2,
            meetingName = "Meeting 2",
            categoryId = CategoryId.GREYHOUND,
            advertisedStart = Instant.fromEpochSeconds(2000),
        ),
    )

    @Test
    fun initialState_isCorrect() = runTest {
        // Given
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Success(emptyList()))
        every { mockDisplayModelMapper.mapToDisplayModel(any()) } returns sampleDisplayRaces[0]

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        // When/Then
        viewModel.uiState.test {
            // Get first state (might be loading or completed depending on timing)
            val firstState = awaitItem()

            if (firstState.isLoading) {
                // If we catch loading state, verify it and wait for completion
                assertTrue(firstState.displayRaces.isEmpty())
                assertTrue(firstState.selectedCategories.isEmpty())
                assertNull(firstState.error)

                val loadedState = awaitItem()
                assertFalse(loadedState.isLoading)
                assertTrue(loadedState.displayRaces.isEmpty())
                assertTrue(loadedState.selectedCategories.isEmpty())
                assertNull(loadedState.error)
            } else {
                // If loading completed immediately, verify final state
                assertFalse(firstState.isLoading)
                assertTrue(firstState.displayRaces.isEmpty())
                assertTrue(firstState.selectedCategories.isEmpty())
                assertNull(firstState.error)
            }
        }
    }

    @Test
    fun loadRaces_success_updatesStateCorrectly() = runTest {
        // Given
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Success(sampleRaces))
        every { mockDisplayModelMapper.mapToDisplayModel(sampleRaces[0]) } returns sampleDisplayRaces[0]
        every { mockDisplayModelMapper.mapToDisplayModel(sampleRaces[1]) } returns sampleDisplayRaces[1]

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        // When/Then
        viewModel.uiState.test {
            // Get first state (might be loading or completed depending on timing)
            val firstState = awaitItem()

            if (firstState.isLoading) {
                // If we catch loading state, wait for success state
                val loadedState = awaitItem()
                assertFalse(loadedState.isLoading)
                assertEquals(2, loadedState.displayRaces.size)
                assertEquals("Meeting 1 R1", loadedState.displayRaces.first().raceName)
                assertNull(loadedState.error)
            } else {
                // If loading completed immediately, verify success state
                assertFalse(firstState.isLoading)
                assertEquals(2, firstState.displayRaces.size)
                assertEquals("Meeting 1 R1", firstState.displayRaces.first().raceName)
                assertNull(firstState.error)
            }
        }
    }

    @Test
    fun loadRaces_error_updatesStateWithError() = runTest {
        // Given
        val error = DataError.NetworkUnavailable
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Error(error))

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        // When/Then
        viewModel.uiState.test {
            // Get first state (might be loading or error depending on timing)
            val firstState = awaitItem()

            if (firstState.isLoading) {
                // If we catch loading state, wait for error state
                val errorState = awaitItem()
                assertFalse(errorState.isLoading)
                assertTrue(errorState.displayRaces.isEmpty())
                assertEquals("Network unavailable", errorState.error)
            } else {
                // If error came immediately, verify it
                assertFalse(firstState.isLoading)
                assertTrue(firstState.displayRaces.isEmpty())
                assertEquals("Network unavailable", firstState.error)
            }
        }
    }

    @Test
    fun loadRaces_error_emitsSideEffect() = runTest {
        // Given
        val error = DataError.Timeout
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Error(error))

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        // When/Then
        viewModel.sideEffect.test {
            val sideEffect = awaitItem()
            assertTrue(sideEffect is RaceSideEffect.ShowError)
            assertEquals("Request timeout", (sideEffect as RaceSideEffect.ShowError).message)
        }
    }

    @Test
    fun refreshRaces_success_emitsRefreshComplete() = runTest {
        // Given
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Success(emptyList()))
        every { mockDisplayModelMapper.mapToDisplayModel(any()) } returns sampleDisplayRaces[0]

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        // When
        viewModel.handleIntent(RaceIntent.RefreshRaces)

        // Then
        viewModel.sideEffect.test {
            val sideEffect = awaitItem()
            assertTrue(sideEffect is RaceSideEffect.ShowRefreshComplete)
        }
    }

    @Test
    fun toggleCategory_addsNewCategory() = runTest {
        // Given
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Success(emptyList()))

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        // When/Then
        viewModel.uiState.test {
            // Wait for initial loading to complete
            val firstState = awaitItem()
            if (firstState.isLoading) {
                awaitItem() // Wait for loaded state
            }

            // Trigger category toggle
            viewModel.handleIntent(RaceIntent.ToggleCategory(CategoryId.HORSE))

            // Consume states until we have a non-loading state with HORSE category
            var finalState: RaceUiState
            do {
                finalState = awaitItem()
            } while (!finalState.selectedCategories.contains(CategoryId.HORSE) || finalState.isLoading)

            // Verify final state
            assertFalse(finalState.isLoading)
            assertTrue(finalState.selectedCategories.contains(CategoryId.HORSE))
            assertEquals(1, finalState.selectedCategories.size)
        }
    }

    @Test
    fun toggleCategory_removesExistingCategory() = runTest {
        // Given
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Success(emptyList()))

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        // When/Then
        viewModel.uiState.test {
            // Wait for initial loading to complete
            val firstState = awaitItem()
            if (firstState.isLoading) {
                awaitItem() // Wait for loaded state
            }

            // Add category
            viewModel.handleIntent(RaceIntent.ToggleCategory(CategoryId.HORSE))

            // Skip states until we have HORSE category
            var stateWithCategory: RaceUiState
            do {
                stateWithCategory = awaitItem()
            } while (!stateWithCategory.selectedCategories.contains(CategoryId.HORSE) || stateWithCategory.isLoading)

            // Remove category
            viewModel.handleIntent(RaceIntent.ToggleCategory(CategoryId.HORSE))

            // Skip states until we have no categories and are not loading
            var finalState: RaceUiState
            do {
                finalState = awaitItem()
            } while (finalState.selectedCategories.contains(CategoryId.HORSE) || finalState.isLoading)

            // Verify final state has no categories
            assertFalse(finalState.isLoading)
            assertFalse(finalState.selectedCategories.contains(CategoryId.HORSE))
            assertTrue(finalState.selectedCategories.isEmpty())
        }
    }

    @Test
    fun clearFilters_resetsSelectedCategories() = runTest {
        // Given
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Success(emptyList()))

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        // When/Then
        viewModel.uiState.test {
            // Wait for initial loading to complete
            val firstState = awaitItem()
            if (firstState.isLoading) {
                awaitItem() // Wait for loaded state
            }

            // Add first category
            viewModel.handleIntent(RaceIntent.ToggleCategory(CategoryId.HORSE))

            // Skip states until we have HORSE category
            var stateWithHorse: RaceUiState
            do {
                stateWithHorse = awaitItem()
            } while (!stateWithHorse.selectedCategories.contains(CategoryId.HORSE) || stateWithHorse.isLoading)

            // Add second category
            viewModel.handleIntent(RaceIntent.ToggleCategory(CategoryId.GREYHOUND))

            // Skip states until we have both categories
            var stateWithBoth: RaceUiState
            do {
                stateWithBoth = awaitItem()
            } while (stateWithBoth.selectedCategories.size < 2 || stateWithBoth.isLoading)

            // Clear all filters
            viewModel.handleIntent(RaceIntent.ClearFilters)

            // After clearFilters, we expect to see states with empty categories
            // There might be a loading state with empty categories, then a final state
            var state: RaceUiState
            do {
                state = awaitItem()
            } while (state.isLoading) // Keep consuming until we get a non-loading state

            // Final state should have no categories and not be loading
            assertFalse(state.isLoading)
            assertTrue(state.selectedCategories.isEmpty())
        }
    }

    @Test
    fun handleIntent_loadRaces_triggersLoading() = runTest {
        // Given
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Success(emptyList()))

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        // When/Then
        viewModel.uiState.test {
            // Wait for initial load to complete
            val firstState = awaitItem()
            if (firstState.isLoading) {
                awaitItem() // Wait for loaded state
            }

            // Trigger another load
            viewModel.handleIntent(RaceIntent.LoadRaces)

            // Should see loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.error)

            // Consume the completion state to avoid unconsumed events
            awaitItem() // loaded state
        }
    }
}
