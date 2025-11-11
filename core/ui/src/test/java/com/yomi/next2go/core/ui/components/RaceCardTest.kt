package com.yomi.next2go.core.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.ui.theme.Next2GoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class RaceCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private object RaceCardProvider {
        fun default(
            raceName: String = "BATHURST",
            raceNumber: Int = 8,
            countdownText: String = "2m 33s",
            categoryId: CategoryId = CategoryId.HORSE,
            categoryName: String = "Horse Racing",
            isLive: Boolean = false,
        ): @androidx.compose.runtime.Composable () -> Unit = {
            Next2GoTheme {
                RaceCard(
                    raceName = raceName,
                    raceNumber = raceNumber,
                    countdownText = countdownText,
                    categoryId = categoryId,
                    categoryName = categoryName,
                    isLive = isLive,
                )
            }
        }

        fun live() = default(
            countdownText = "LIVE",
            isLive = true,
        )

        fun greyhound() = default(
            categoryId = CategoryId.GREYHOUND,
            categoryName = "Greyhound Racing",
            raceName = "KILKENNY",
        )
    }

    @Test
    fun raceCard_displaysRaceName() {
        composeTestRule.setContent(RaceCardProvider.default())

        composeTestRule
            .onNodeWithText("BATHURST")
            .assertIsDisplayed()
    }

    @Test
    fun raceCard_displaysCategoryName() {
        composeTestRule.setContent(RaceCardProvider.default())

        composeTestRule
            .onNodeWithText("Horse Racing")
            .assertIsDisplayed()
    }

    @Test
    fun raceCard_displaysRaceNumber() {
        composeTestRule.setContent(RaceCardProvider.default())

        composeTestRule
            .onNodeWithText("R8")
            .assertIsDisplayed()
    }

    @Test
    fun raceCard_displaysCountdown() {
        composeTestRule.setContent(RaceCardProvider.default())

        composeTestRule
            .onNodeWithText("2m 33s")
            .assertIsDisplayed()
    }

    @Test
    fun raceCard_displaysLiveIndicator_whenLive() {
        composeTestRule.setContent(RaceCardProvider.live())

        composeTestRule
            .onNodeWithText("LIVE")
            .assertIsDisplayed()
    }

    @Test
    fun raceCard_displaysGreyhoundCategory() {
        composeTestRule.setContent(RaceCardProvider.greyhound())

        composeTestRule
            .onNodeWithText("Greyhound Racing")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("KILKENNY")
            .assertIsDisplayed()
    }
}
