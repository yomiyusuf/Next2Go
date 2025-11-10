package com.yomi.next2go.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yomi.next2go.core.ui.theme.LocalDimensions
import com.yomi.next2go.core.ui.theme.LocalSpacing
import com.yomi.next2go.core.ui.theme.Next2GoTheme

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val dimensions = LocalDimensions.current

    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val borderColor = if (isSelected) {
        Color.Transparent
    } else {
        MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = modifier
            .height(dimensions.filterChipHeight)
            .clip(RoundedCornerShape(dimensions.filterChipCornerRadius))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(dimensions.filterChipCornerRadius),
            )
            .clickable(role = Role.Checkbox) { onClick() }
            .semantics { selected = isSelected }
            .padding(horizontal = spacing.large),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(spacing.small))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterChipPreview() {
    Next2GoTheme {
        Row {
            FilterChip(
                text = "Horse Racing",
                isSelected = false,
                onClick = { },
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                text = "Greyhound",
                isSelected = true,
                onClick = { },
            )
        }
    }
}
