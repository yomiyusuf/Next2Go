package com.yomi.next2go.core.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 20.dp,
    val xxLarge: Dp = 24.dp,
    val xxxLarge: Dp = 32.dp,
)

@Stable
data class Dimensions(
    // Race card dimensions
    val raceCardMinHeight: Dp = 120.dp,
    val raceCardElevation: Dp = 2.dp,
    val raceCardCornerRadius: Dp = 8.dp,

    // Filter chip dimensions
    val filterChipHeight: Dp = 40.dp,
    val filterChipCornerRadius: Dp = 20.dp,

    // Category indicator
    val categoryIndicatorSize: Dp = 16.dp,

    // Race number circle
    val raceNumberSize: Dp = 24.dp,

    // Odds button
    val oddsButtonWidth: Dp = 64.dp,
    val oddsButtonHeight: Dp = 32.dp,
    val oddsButtonCornerRadius: Dp = 4.dp,

    // Countdown timer
    val countdownMinWidth: Dp = 48.dp,
)

val LocalSpacing = androidx.compose.runtime.compositionLocalOf { Spacing() }
val LocalDimensions = androidx.compose.runtime.compositionLocalOf { Dimensions() }
