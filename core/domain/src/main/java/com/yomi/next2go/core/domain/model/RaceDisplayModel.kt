package com.yomi.next2go.core.domain.model

data class RaceDisplayModel(
    val id: String,
    val raceName: String,
    val raceNumber: Int,
    val runnerName: String,
    val runnerNumber: Int,
    val jockeyName: String,
    val bestTime: String,
    val odds: String,
    val countdownText: String, //Pre-formatted: "2m 33s", "LIVE", etc.
    val categoryColor: CategoryColor,
    val isLive: Boolean
)