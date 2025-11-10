package com.yomi.next2go.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yomi.next2go.core.ui.theme.CategoryGreen
import com.yomi.next2go.core.ui.theme.LiveBlue
import com.yomi.next2go.core.ui.theme.LocalDimensions
import com.yomi.next2go.core.ui.theme.LocalSpacing
import com.yomi.next2go.core.ui.theme.Next2GoTheme
import com.yomi.next2go.core.ui.theme.Orange500
import com.yomi.next2go.core.ui.theme.TextSecondary

@Composable
fun RaceCard(
    raceName: String,
    raceNumber: Int,
    runnerName: String,
    runnerNumber: Int,
    jockeyName: String,
    bestTime: String,
    odds: String,
    countdownText: String,
    categoryColor: Color,
    isLive: Boolean,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val dimensions = LocalDimensions.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = dimensions.raceCardMinHeight),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.raceCardElevation),
        shape = RoundedCornerShape(dimensions.raceCardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.large),
        ) {
            // Header row with race name and countdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category indicator
                    Box(
                        modifier = Modifier
                            .size(dimensions.categoryIndicatorSize)
                            .clip(CircleShape)
                            .background(categoryColor)
                            .semantics { contentDescription = "Racing category indicator" },
                    )

                    Spacer(modifier = Modifier.width(spacing.small))

                    Text(
                        text = raceName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                // Countdown or LIVE indicator
                Text(
                    text = countdownText,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = if (isLive) LiveBlue else Orange500,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            // Runner info section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.small),
                ) {
                    Text(
                        text = "NO. / RUNNER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                        ),
                    )

                    // Race number circle and runner info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(dimensions.raceNumberSize)
                                .clip(CircleShape)
                                .background(categoryColor),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = runnerNumber.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                ),
                            )
                        }

                        Spacer(modifier = Modifier.width(spacing.small))

                        Text(
                            text = "$runnerNumber. $runnerName (Fr$raceNumber)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    Text(
                        text = "D: $jockeyName",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                        ),
                    )

                    Text(
                        text = "Best Time: $bestTime",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                        ),
                    )
                }

                // Odds button - aligned to center-top of runner section
                Box(
                    modifier = Modifier
                        .width(dimensions.oddsButtonWidth)
                        .height(dimensions.oddsButtonHeight)
                        .clip(RoundedCornerShape(dimensions.oddsButtonCornerRadius))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(dimensions.oddsButtonCornerRadius),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = odds,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RaceCardPreview() {
    Next2GoTheme {
        Column {
            RaceCard(
                raceName = "BATHURST R8",
                raceNumber = 8,
                runnerName = "Snipers Fire",
                runnerNumber = 6,
                jockeyName = "Brad Hewitt",
                bestTime = "16.11",
                odds = "1.35",
                countdownText = "2m 33s",
                categoryColor = CategoryGreen,
                isLive = false,
            )

            Spacer(modifier = Modifier.height(8.dp))

            RaceCard(
                raceName = "KILKENNY R1",
                raceNumber = 1,
                runnerName = "Kilkenny Swift",
                runnerNumber = 6,
                jockeyName = "Sarah Johnson",
                bestTime = "29.13",
                odds = "4.00",
                countdownText = "LIVE",
                categoryColor = CategoryGreen,
                isLive = true,
            )
        }
    }
}
