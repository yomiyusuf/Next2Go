package com.yomi.next2go.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.yomi.next2go.core.domain.model.CategoryColor
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.model.RaceDisplayModel
import com.yomi.next2go.mvi.RaceIntent
import com.yomi.next2go.mvi.RaceUiState
import com.yomi.next2go.core.ui.theme.Next2GoTheme
import com.yomi.next2go.ui.screens.RaceScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class RaceScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private object RaceScreenProvider {

        val sampleDisplayRaces = listOf(
            RaceDisplayModel(
                categoryId = CategoryId.HORSE,
                id = "1",
                raceName = "BATHURST R8",
                raceNumber = 8,
                runnerName = "Next Runner",
                runnerNumber = 1,
                countdownText = "3m 0s",
                categoryColor = CategoryColor.GREEN,
                isLive = false,
                contentDescription = "",
            ),
            RaceDisplayModel(
                categoryId = CategoryId.HORSE,
                id = "2",
                raceName = "CANNINGTON R2",
                raceNumber = 2,
                runnerName = "Next Runner",
                runnerNumber = 1,
                countdownText = "5m 0s",
                categoryColor = CategoryColor.RED,
                isLive = false,
                contentDescription = ""
            ),
        )

        fun default(
            uiState: RaceUiState = RaceUiState(
                displayRaces = sampleDisplayRaces,
                isLoading = false,
            ),
            onIntent: (RaceIntent) -> Unit = { },
        ): @Composable () -> Unit = {
            Next2GoTheme {
                RaceScreen(
                    uiState = uiState,
                    onIntent = onIntent,
                )
            }
        }

        fun loading() = default(
            uiState = RaceUiState(isLoading = true),
        )

        fun error(
            onIntent: (RaceIntent) -> Unit = { },
        ) = default(
            uiState = RaceUiState(
                error = "Network error occurred",
                isLoading = false,
            ),
            onIntent = onIntent,
        )

        fun withSelectedCategories(categories: Set<CategoryId>) = default(
            uiState = RaceUiState(
                displayRaces = sampleDisplayRaces,
                selectedCategories = categories,
                isLoading = false,
            ),
        )
    }

    @Test
    fun raceScreen_displaysTitle() {
        composeTestRule.setContent(RaceScreenProvider.default())

        composeTestRule
            .onNodeWithText("Next2Go Racing")
            .assertIsDisplayed()
    }

    @Test
    fun raceScreen_displaysCategoryFilters() {
        composeTestRule.setContent(RaceScreenProvider.default())

        composeTestRule
            .onNodeWithText("Horse Racing")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Greyhound Racing")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Harness Racing")
            .assertIsDisplayed()
    }

    @Test
    fun raceScreen_displaysRaceList() {
        composeTestRule.setContent(RaceScreenProvider.default())

        composeTestRule
            .onNodeWithText("BATHURST R8")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("CANNINGTON R2")
            .assertIsDisplayed()
    }

    @Test
    fun raceScreen_displaysLoadingState() {
        composeTestRule.setContent(RaceScreenProvider.loading())
        composeTestRule
            .onNodeWithText("BATHURST R8")
            .assertDoesNotExist()
    }

    @Test
    fun raceScreen_displaysErrorState() {
        composeTestRule.setContent(RaceScreenProvider.error())

        composeTestRule
            .onNodeWithText("Network error occurred")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Retry")
            .assertIsDisplayed()
    }

    @Test
    fun raceScreen_callsOnIntentWhenCategorySelected() {
        var capturedIntent: RaceIntent? = null

        composeTestRule.setContent(
            RaceScreenProvider.default(onIntent = { capturedIntent = it }),
        )

        composeTestRule
            .onNodeWithText("Horse Racing")
            .performClick()

        assert(capturedIntent is RaceIntent.ToggleCategory)
        assert((capturedIntent as RaceIntent.ToggleCategory).category == CategoryId.HORSE)
    }

    @Test
    fun raceScreen_showsSelectedCategoryState() {
        composeTestRule.setContent(
            RaceScreenProvider.withSelectedCategories(setOf(CategoryId.HORSE)),
        )

        composeTestRule
            .onNodeWithText("Horse Racing")
            .assertIsDisplayed()
    }

    @Test
    fun raceScreen_callsOnIntentWhenRetryPressed() {
        var capturedIntent: RaceIntent? = null

        composeTestRule.setContent(
            RaceScreenProvider.error(onIntent = { capturedIntent = it }),
        )

        composeTestRule
            .onNodeWithText("Retry")
            .performClick()

        assert(capturedIntent is RaceIntent.LoadRaces)
    }
}
