package com.yomi.next2go.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.ui.theme.CategoryGreen
import com.yomi.next2go.core.ui.theme.CategoryRed
import com.yomi.next2go.core.ui.theme.CategoryYellow
import com.yomi.next2go.core.ui.theme.LiveBlue
import com.yomi.next2go.core.ui.theme.LocalSpacing
import com.yomi.next2go.core.ui.theme.Next2GoTheme
import com.yomi.next2go.core.ui.theme.Orange500

@Composable
fun RaceCard(
    raceName: String,
    raceNumber: Int,
    countdownText: String,
    categoryId: CategoryId,
    isLive: Boolean,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val categoryInfo = getCategoryInfo(categoryId)

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            // Top row: Race type emoji + name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = categoryInfo.emoji,
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.width(spacing.small))

                Text(
                    text = categoryInfo.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                )
            }

            // Second row: Race number + meeting name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Race number in rounded box
                Box(
                    modifier = Modifier
                        .background(
                            color = categoryInfo.color,
                            shape = RoundedCornerShape(6.dp),
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "R$raceNumber",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.width(spacing.small))

                // Meeting name
                Text(
                    text = raceName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
            }

            // Bottom row: Countdown
            Text(
                text = countdownText,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = if (isLive) LiveBlue else Orange500,
                    fontWeight = FontWeight.Bold,
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private data class CategoryInfo(
    val name: String,
    val emoji: String,
    val color: Color,
)

@Composable
private fun getCategoryInfo(categoryId: CategoryId): CategoryInfo {
    return when (categoryId) {
        CategoryId.HORSE -> CategoryInfo(
            name = "Horse Racing",
            emoji = "\uD83C\uDFC7",
            color = CategoryGreen,
        )
        CategoryId.GREYHOUND -> CategoryInfo(
            name = "Greyhound Racing",
            emoji = "\uD83E\uDDAE",
            color = CategoryRed,
        )
        CategoryId.HARNESS -> CategoryInfo(
            name = "Harness Racing",
            emoji = "\uD83D\uDE83",
            color = CategoryYellow,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RaceCardPreview() {
    Next2GoTheme {
        Column {
            RaceCard(
                raceName = "BATHURST",
                raceNumber = 8,
                countdownText = "2m 33s",
                categoryId = CategoryId.HORSE,
                isLive = false,
            )

            Spacer(modifier = Modifier.height(8.dp))

            RaceCard(
                raceName = "KILKENNY",
                raceNumber = 1,
                countdownText = "LIVE",
                categoryId = CategoryId.GREYHOUND,
                isLive = true,
            )

            Spacer(modifier = Modifier.height(8.dp))

            RaceCard(
                raceName = "ALBION PARK",
                raceNumber = 5,
                countdownText = "1m 15s",
                categoryId = CategoryId.HARNESS,
                isLive = false,
            )
        }
    }
}
