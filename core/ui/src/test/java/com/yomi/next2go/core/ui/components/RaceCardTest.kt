package com.yomi.next2go.core.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.yomi.next2go.core.ui.theme.Next2GoTheme
import org.junit.Rule
import org.junit.Test

class RaceCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private object RaceCardProvider {
        fun default(
            raceName: String = "BATHURST R8",
            raceNumber: Int = 8,
            runnerName: String = "Snipers Fire",
            runnerNumber: Int = 6,
            jockeyName: String = "Brad Hewitt",
            bestTime: String = "16.11",
            odds: String = "1.35",
            countdownText: String = "2m 33s",
            categoryColor: Color = Color.Green,
            isLive: Boolean = false,
        ): @androidx.compose.runtime.Composable () -> Unit = {
            Next2GoTheme {
                RaceCard(
                    raceName = raceName,
                    raceNumber = raceNumber,
                    runnerName = runnerName,
                    runnerNumber = runnerNumber,
                    jockeyName = jockeyName,
                    bestTime = bestTime,
                    odds = odds,
                    countdownText = countdownText,
                    categoryColor = categoryColor,
                    isLive = isLive
                )
            }
        }

        fun live() = default(
            countdownText = "LIVE",
            isLive = true
        )

        fun withCustomData(
            runnerName: String,
            odds: String
        ) = default(
            runnerName = runnerName,
            odds = odds
        )
    }

    @Test
    fun raceCard_displaysRaceName() {
        composeTestRule.setContent(RaceCardProvider.default())

        composeTestRule
            .onNodeWithText("BATHURST R8")
            .assertIsDisplayed()
    }

    @Test
    fun raceCard_displaysRunnerDetails() {
        composeTestRule.setContent(RaceCardProvider.default())

        composeTestRule
            .onNodeWithText("6. Snipers Fire (Fr8)")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("D: Brad Hewitt")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Best Time: 16.11")
            .assertIsDisplayed()
    }

    @Test
    fun raceCard_displaysOdds() {
        composeTestRule.setContent(RaceCardProvider.default())

        composeTestRule
            .onNodeWithText("1.35")
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
    fun raceCard_displaysCategoryIndicator() {
        composeTestRule.setContent(RaceCardProvider.default())

        composeTestRule
            .onNodeWithContentDescription("Racing category indicator")
            .assertIsDisplayed()
    }

    @Test
    fun raceCard_displaysCustomRunnerData() {
        composeTestRule.setContent(
            RaceCardProvider.withCustomData(
                runnerName = "Thunder Bolt",
                odds = "3.50"
            )
        )

        composeTestRule
            .onNodeWithText("6. Thunder Bolt (Fr8)")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("3.50")
            .assertIsDisplayed()
    }
}