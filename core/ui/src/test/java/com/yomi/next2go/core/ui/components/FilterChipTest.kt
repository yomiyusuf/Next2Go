package com.yomi.next2go.core.ui.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.yomi.next2go.core.ui.theme.Next2GoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class FilterChipTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private object FilterChipProvider {
        fun default(
            text: String = "Horse Racing",
            isSelected: Boolean = false,
            onClick: () -> Unit = { }
        ): @androidx.compose.runtime.Composable () -> Unit = {
            Next2GoTheme {
                FilterChip(
                    text = text,
                    isSelected = isSelected,
                    onClick = onClick
                )
            }
        }

        fun selected(text: String = "Horse Racing") = default(
            text = text,
            isSelected = true
        )

        fun unselected(text: String = "Horse Racing") = default(
            text = text,
            isSelected = false
        )
    }

    @Test
    fun filterChip_displaysText() {
        composeTestRule.setContent(FilterChipProvider.default())

        composeTestRule
            .onNodeWithText("Horse Racing")
            .assertIsDisplayed()
    }

    @Test
    fun filterChip_isClickable() {
        composeTestRule.setContent(FilterChipProvider.default())

        composeTestRule
            .onNodeWithText("Horse Racing")
            .assertHasClickAction()
    }

    @Test
    fun filterChip_showsUnselectedState() {
        composeTestRule.setContent(FilterChipProvider.unselected())

        composeTestRule
            .onNodeWithText("Horse Racing")
            .assertIsNotSelected()
    }

    @Test
    fun filterChip_showsSelectedState() {
        composeTestRule.setContent(FilterChipProvider.selected())

        composeTestRule
            .onNodeWithText("Horse Racing")
            .assertIsSelected()
    }

    @Test
    fun filterChip_callsOnClickWhenTapped() {
        var clicked = false
        
        composeTestRule.setContent(
            FilterChipProvider.default(onClick = { clicked = true })
        )

        composeTestRule
            .onNodeWithText("Horse Racing")
            .performClick()

        assert(clicked)
    }

    @Test
    fun filterChip_displaysCustomText() {
        composeTestRule.setContent(
            FilterChipProvider.default(text = "Greyhound Racing")
        )

        composeTestRule
            .onNodeWithText("Greyhound Racing")
            .assertIsDisplayed()
    }
}