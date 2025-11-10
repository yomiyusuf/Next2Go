package com.yomi.next2go.core.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yomi.next2go.core.domain.model.CategoryColor
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.RaceDisplayModel
import com.yomi.next2go.core.domain.mvi.RaceIntent
import com.yomi.next2go.core.domain.mvi.RaceUiState
import com.yomi.next2go.core.ui.components.FilterChip
import com.yomi.next2go.core.ui.components.RaceCard
import com.yomi.next2go.core.ui.theme.CategoryGreen
import com.yomi.next2go.core.ui.theme.CategoryRed
import com.yomi.next2go.core.ui.theme.CategoryYellow
import com.yomi.next2go.core.ui.theme.DarkBackground
import com.yomi.next2go.core.ui.theme.LocalSpacing
import com.yomi.next2go.core.ui.theme.Next2GoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceScreen(
    uiState: RaceUiState,
    onIntent: (RaceIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBackground)
                .padding(spacing.large),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = "NEXT TO GO RACING",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.large),
        ) {
            // Filter Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
                contentPadding = PaddingValues(horizontal = 0.dp),
            ) {
                items(
                    listOf(
                        CategoryId.HORSE to "Horse Racing",
                        CategoryId.GREYHOUND to "Greyhound Racing",
                        CategoryId.HARNESS to "Harness Racing",
                    ),
                ) { (categoryId, text) ->
                    FilterChip(
                        text = text,
                        isSelected = uiState.selectedCategories.contains(categoryId),
                        onClick = { onIntent(RaceIntent.ToggleCategory(categoryId)) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.large))

            // Content Area
            when {
                uiState.isLoading -> {
                    LoadingState(modifier = Modifier.fillMaxSize())
                }
                uiState.error != null -> {
                    ErrorState(
                        error = uiState.error ?: "Unknown error",
                        onRetry = { onIntent(RaceIntent.LoadRaces) },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                uiState.displayRaces.isEmpty() -> {
                    EmptyState(modifier = Modifier.fillMaxSize())
                }
                else -> {
                    RaceList(
                        displayRaces = uiState.displayRaces,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error,
            )

            Spacer(modifier = Modifier.height(spacing.large))

            Button(
                onClick = onRetry,
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No races available",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RaceList(
    displayRaces: List<RaceDisplayModel>,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = spacing.large),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
    ) {
        items(displayRaces) { displayRace ->
            RaceCard(
                raceName = displayRace.raceName,
                raceNumber = displayRace.raceNumber,
                runnerName = displayRace.runnerName,
                runnerNumber = displayRace.runnerNumber,
                jockeyName = displayRace.jockeyName,
                bestTime = displayRace.bestTime,
                odds = displayRace.odds,
                countdownText = displayRace.countdownText,
                categoryColor = getCategoryColor(displayRace.categoryColor),
                isLive = displayRace.isLive,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RaceScreenPreview() {
    Next2GoTheme {
        RaceScreen(
            uiState = RaceUiState(
                displayRaces = listOf(
                    RaceDisplayModel(
                        id = "1",
                        raceName = "BATHURST R8",
                        raceNumber = 8,
                        runnerName = "Next Runner",
                        runnerNumber = 1,
                        jockeyName = "TBA",
                        bestTime = "--:--",
                        odds = "--",
                        countdownText = "3m 0s",
                        categoryColor = CategoryColor.GREEN,
                        isLive = false,
                    ),
                    RaceDisplayModel(
                        id = "2",
                        raceName = "CANNINGTON R2",
                        raceNumber = 2,
                        runnerName = "Next Runner",
                        runnerNumber = 1,
                        jockeyName = "TBA",
                        bestTime = "--:--",
                        odds = "--",
                        countdownText = "5m 0s",
                        categoryColor = CategoryColor.RED,
                        isLive = false,
                    ),
                ),
                selectedCategories = setOf(CategoryId.HORSE),
                isLoading = false,
            ),
            onIntent = { },
        )
    }
}

private fun getCategoryColor(categoryColor: CategoryColor): Color {
    return when (categoryColor) {
        CategoryColor.GREEN -> CategoryGreen
        CategoryColor.RED -> CategoryRed
        CategoryColor.YELLOW -> CategoryYellow
    }
}
