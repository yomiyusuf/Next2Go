package com.yomi.next2go.model

import com.yomi.next2go.core.domain.model.CategoryColor
import com.yomi.next2go.core.domain.model.CategoryId

data class RaceDisplayModel(
    val id: String,
    val raceName: String,
    val raceNumber: Int,
    val runnerName: String,
    val runnerNumber: Int,
    // Pre-formatted: "2m 33s", "LIVE", etc.
    val countdownText: String,
    val categoryColor: CategoryColor,
    val categoryId: CategoryId,
    val isLive: Boolean,
    // Accessibility content description for screen readers
    val contentDescription: String,
)
