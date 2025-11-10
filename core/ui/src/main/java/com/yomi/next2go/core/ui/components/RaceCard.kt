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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.ui.theme.LiveBlue
import com.yomi.next2go.core.ui.theme.LocalSpacing
import com.yomi.next2go.core.ui.theme.Next2GoTheme
import com.yomi.next2go.core.ui.theme.Orange500
import com.yomi.next2go.core.ui.util.toUiColor

@Composable
fun RaceCard(
    raceName: String,
    raceNumber: Int,
    countdownText: String,
    categoryId: CategoryId,
    categoryName: String,
    isLive: Boolean,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    val categoryEmoji = categoryId.emoji
    val categoryColor = categoryId.categoryColor.toUiColor()
    
    val contentDescription = buildString {
        append("$categoryName race ")
        append("number $raceNumber ")
        append("at $raceName. ")
        if (isLive) {
            append("Race is currently live.")
        } else {
            append("Starting in $countdownText.")
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                this.contentDescription = contentDescription
            },
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
                    text = categoryEmoji,
                    style = MaterialTheme.typography.titleLarge,
                )

                Spacer(modifier = Modifier.width(spacing.small))

                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
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
                            color = categoryColor,
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
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
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
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
            )
        }
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
                categoryName = "Horse Racing",
                isLive = false,
            )

            Spacer(modifier = Modifier.height(8.dp))

            RaceCard(
                raceName = "KILKENNY",
                raceNumber = 1,
                countdownText = "LIVE",
                categoryId = CategoryId.GREYHOUND,
                categoryName = "Greyhound Racing",
                isLive = true,
            )

            Spacer(modifier = Modifier.height(8.dp))

            RaceCard(
                raceName = "ALBION PARK",
                raceNumber = 5,
                countdownText = "1m 15s",
                categoryId = CategoryId.HARNESS,
                categoryName = "Harness Racing",
                isLive = false,
            )
        }
    }
}

