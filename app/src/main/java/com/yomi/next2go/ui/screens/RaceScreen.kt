package com.yomi.next2go.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.lazy.LazyItemScope
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.yomi.next2go.R
import com.yomi.next2go.core.domain.model.CategoryColor
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.model.RaceDisplayModel
import com.yomi.next2go.mvi.RaceIntent
import com.yomi.next2go.mvi.RaceUiState
import com.yomi.next2go.core.ui.components.FilterChip
import com.yomi.next2go.core.ui.components.RaceCard
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
                text = "Next2Go Racing",
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
                    listOf(CategoryId.HORSE, CategoryId.GREYHOUND, CategoryId.HARNESS),
                ) { categoryId ->
                    val categoryName = stringResource(id = getCategoryStringResource(categoryId))
                    FilterChip(
                        text = categoryName,
                        isSelected = uiState.selectedCategories.contains(categoryId),
                        onClick = { onIntent(RaceIntent.ToggleCategory(categoryId)) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.large))

            // Content Area
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading && uiState.displayRaces.isEmpty() -> {
                        LoadingState(modifier = Modifier.fillMaxSize())
                    }
                    uiState.error != null && uiState.displayRaces.isEmpty() -> {
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

                // Subtle refresh indicator overlay
                if (uiState.isRefreshing && uiState.displayRaces.isNotEmpty()) {
                    RefreshIndicator(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = spacing.small)
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
        modifier = modifier.semantics {
            contentDescription = "No races currently available. Try refreshing or changing your filters."
        },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No races available",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RefreshIndicator(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .semantics {
                contentDescription = "Refreshing race data in background"
            },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.size(16.dp),
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Refreshing...",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun RaceList(
    displayRaces: List<RaceDisplayModel>,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    LazyColumn(
        modifier = modifier.semantics {
            contentDescription = "List of ${displayRaces.size} upcoming races"
        },
        contentPadding = PaddingValues(bottom = spacing.large),
        verticalArrangement = Arrangement.spacedBy(spacing.medium),
    ) {
        items(
            items = displayRaces,
            key = { displayRace -> displayRace.id }
        ) { displayRace ->
            val categoryName = stringResource(id = getCategoryStringResource(displayRace.categoryId))
            AnimatedRaceCard(
                raceName = displayRace.raceName,
                raceNumber = displayRace.raceNumber,
                countdownText = displayRace.countdownText,
                categoryId = displayRace.categoryId,
                categoryName = categoryName,
                isLive = displayRace.isLive,
            )
        }
    }
}

@Composable
private fun AnimatedRaceCard(
    raceName: String,
    raceNumber: Int,
    countdownText: String,
    categoryId: CategoryId,
    categoryName: String,
    isLive: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 300)
        ) + slideInVertically(
            animationSpec = tween(durationMillis = 300),
            initialOffsetY = { -it / 4 }
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 200)
        ) + slideOutVertically(
            animationSpec = tween(durationMillis = 200),
            targetOffsetY = { it / 4 }
        ),
        modifier = modifier
    ) {
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

@Preview(showBackground = true)
@Composable
fun RaceScreenPreview() {
    Next2GoTheme {
        RaceScreen(
            uiState = RaceUiState(
                displayRaces = listOf(
                    RaceDisplayModel(
                        id = "1",
                        raceName = "BATHURST",
                        raceNumber = 8,
                        runnerName = "Next Runner",
                        runnerNumber = 1,
                        countdownText = "3m 0s",
                        categoryColor = CategoryColor.GREEN,
                        categoryId = CategoryId.HORSE,
                        isLive = false,
                        contentDescription = "Horse Racing race number 8 at BATHURST. Starting in 3m 0s.",
                    ),
                    RaceDisplayModel(
                        id = "2",
                        raceName = "CANNINGTON",
                        raceNumber = 2,
                        runnerName = "Next Runner",
                        runnerNumber = 1,
                        countdownText = "5m 0s",
                        categoryColor = CategoryColor.RED,
                        categoryId = CategoryId.GREYHOUND,
                        isLive = false,
                        contentDescription = "Greyhound Racing race number 2 at CANNINGTON. Starting in 5m 0s.",
                    ),
                ),
                selectedCategories = setOf(CategoryId.HORSE),
                isLoading = false,
            ),
            onIntent = { },
        )
    }
}

private fun getCategoryStringResource(categoryId: CategoryId): Int {
    return when (categoryId) {
        CategoryId.HORSE -> R.string.category_horse_racing
        CategoryId.GREYHOUND -> R.string.category_greyhound_racing
        CategoryId.HARNESS -> R.string.category_harness_racing
    }
}
