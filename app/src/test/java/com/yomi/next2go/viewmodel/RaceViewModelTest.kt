package com.yomi.next2go.viewmodel

import app.cash.turbine.test
import com.yomi.next2go.core.domain.model.CategoryColor
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.DataError
import com.yomi.next2go.core.domain.model.Race
import com.yomi.next2go.core.domain.repository.Result
import com.yomi.next2go.core.domain.timer.CountdownTimer
import com.yomi.next2go.core.domain.usecase.GetNextRacesUseCase
import com.yomi.next2go.mapper.RaceDisplayModelMapper
import com.yomi.next2go.model.RaceDisplayModel
import com.yomi.next2go.mvi.RaceIntent
import com.yomi.next2go.mvi.RaceSideEffect
import com.yomi.next2go.mvi.RaceUiState
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
            raceName = "Meeting 1",
            raceNumber = 1,
            runnerName = "Next Runner",
            runnerNumber = 1,
            countdownText = "5m 0s",
            categoryColor = CategoryColor.GREEN,
            categoryId = CategoryId.HORSE,
            isLive = false,
            contentDescription = "Horse Racing race number 1 at Meeting 1. Starting in 5m 0s.",
        ),
        RaceDisplayModel(
            id = "2",
            raceName = "Meeting 2",
            raceNumber = 2,
            runnerName = "Next Runner",
            runnerNumber = 1,
            countdownText = "10m 0s",
            categoryColor = CategoryColor.RED,
            categoryId = CategoryId.GREYHOUND,
            isLive = false,
            contentDescription = "Greyhound Racing race number 2 at Meeting 2. Starting in 10m 0s.",
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
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Success(emptyList()))
        every { mockDisplayModelMapper.mapToDisplayModel(any()) } returns sampleDisplayRaces[0]

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        viewModel.uiState.test {
            val firstState = awaitItem()

            if (firstState.isLoading) {
                assertTrue(firstState.displayRaces.isEmpty())
                assertTrue(firstState.selectedCategories.isEmpty())
                assertNull(firstState.error)

                val loadedState = awaitItem()
                assertFalse(loadedState.isLoading)
                assertTrue(loadedState.displayRaces.isEmpty())
                assertTrue(loadedState.selectedCategories.isEmpty())
                assertNull(loadedState.error)
            } else {
                assertFalse(firstState.isLoading)
                assertTrue(firstState.displayRaces.isEmpty())
                assertTrue(firstState.selectedCategories.isEmpty())
                assertNull(firstState.error)
            }
        }
    }

    @Test
    fun loadRaces_success_updatesStateCorrectly() = runTest {
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Success(sampleRaces))
        every { mockDisplayModelMapper.mapToDisplayModel(sampleRaces[0]) } returns sampleDisplayRaces[0]
        every { mockDisplayModelMapper.mapToDisplayModel(sampleRaces[1]) } returns sampleDisplayRaces[1]

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        viewModel.uiState.test {
            val firstState = awaitItem()

            if (firstState.isLoading) {
                val loadedState = awaitItem()
                assertFalse(loadedState.isLoading)
                assertEquals(2, loadedState.displayRaces.size)
                assertEquals("Meeting 1", loadedState.displayRaces.first().raceName)
                assertNull(loadedState.error)
            } else {
                assertFalse(firstState.isLoading)
                assertEquals(2, firstState.displayRaces.size)
                assertEquals("Meeting 1", firstState.displayRaces.first().raceName)
                assertNull(firstState.error)
            }
        }
    }

    @Test
    fun toggleCategory_addsNewCategory() = runTest {
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Success(emptyList()))

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(RaceIntent.ToggleCategory(CategoryId.HORSE))
        testScheduler.advanceUntilIdle()

        val finalState = viewModel.uiState.value
        assertTrue(finalState.selectedCategories.contains(CategoryId.HORSE))
        assertEquals(1, finalState.selectedCategories.size)
    }

    @Test
    fun loadRaces_error_updatesStateAndEmitsSideEffect() = runTest {
        val error = DataError.NetworkUnavailable
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Error(error))

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        viewModel.uiState.test {
            val firstState = awaitItem()

            if (firstState.isLoading) {
                val errorState = awaitItem()
                assertFalse(errorState.isLoading)
                assertTrue(errorState.displayRaces.isEmpty())
                assertEquals(error.message, errorState.error)
            } else {
                assertFalse(firstState.isLoading)
                assertTrue(firstState.displayRaces.isEmpty())
                assertEquals(error.message, firstState.error)
            }
        }

        viewModel.sideEffect.test {
            val sideEffect = awaitItem()
            assertEquals(RaceSideEffect.ShowError(error.message), sideEffect)
        }
    }

    @Test
    fun refreshRaces_success_updatesStateAndEmitsRefreshComplete() = runTest {
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Success(sampleRaces))
        every { mockDisplayModelMapper.mapToDisplayModel(sampleRaces[0]) } returns sampleDisplayRaces[0]
        every { mockDisplayModelMapper.mapToDisplayModel(sampleRaces[1]) } returns sampleDisplayRaces[1]

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        viewModel.uiState.test {
            val firstState = awaitItem()
            if (firstState.isLoading) {
                awaitItem()
            }

            viewModel.handleIntent(RaceIntent.RefreshRaces)

            var refreshState: RaceUiState
            do {
                refreshState = awaitItem()
            } while (refreshState.isRefreshing)

            assertFalse(refreshState.isLoading)
            assertFalse(refreshState.isRefreshing)
            assertEquals(2, refreshState.displayRaces.size)
            assertNull(refreshState.error)
        }

        viewModel.sideEffect.test {
            val sideEffect = awaitItem()
            assertEquals(RaceSideEffect.ShowRefreshComplete, sideEffect)
        }
    }

    @Test
    fun clearFilters_removesAllSelectedCategories() = runTest {
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Success(emptyList()))

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(RaceIntent.ToggleCategory(CategoryId.HORSE))
        testScheduler.advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value.selectedCategories.contains(CategoryId.HORSE))
        
        viewModel.handleIntent(RaceIntent.ClearFilters)
        testScheduler.advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value.selectedCategories.isEmpty())
    }

    @Test
    fun toggleCategory_removesExistingCategory() = runTest {
        every { mockUseCase.executeStream(any(), any()) } returns flowOf(Result.Success(emptyList()))

        val viewModel = RaceViewModel(mockUseCase, mockDisplayModelMapper, mockCountdownTimer)

        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(RaceIntent.ToggleCategory(CategoryId.HORSE))
        testScheduler.advanceUntilIdle()
        
        assertTrue(viewModel.uiState.value.selectedCategories.contains(CategoryId.HORSE))
        
        viewModel.handleIntent(RaceIntent.ToggleCategory(CategoryId.HORSE))
        testScheduler.advanceUntilIdle()
        
        assertFalse(viewModel.uiState.value.selectedCategories.contains(CategoryId.HORSE))
        assertTrue(viewModel.uiState.value.selectedCategories.isEmpty())
    }

}
