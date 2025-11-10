package com.yomi.next2go.core.ui.util

import androidx.compose.ui.graphics.Color
import com.yomi.next2go.core.domain.model.CategoryColor
import com.yomi.next2go.core.ui.theme.CategoryGreen
import com.yomi.next2go.core.ui.theme.CategoryRed
import com.yomi.next2go.core.ui.theme.CategoryYellow

fun CategoryColor.toUiColor(): Color {
    return when (this) {
        CategoryColor.GREEN -> CategoryGreen
        CategoryColor.RED -> CategoryRed
        CategoryColor.YELLOW -> CategoryYellow
    }
}