package com.yomi.next2go.mapper

import android.content.Context
import com.yomi.next2go.R
import com.yomi.next2go.core.common.time.Clock
import com.yomi.next2go.core.domain.formatter.RaceCountdownFormatter
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.Race
import com.yomi.next2go.model.RaceDisplayModel

class RaceDisplayModelMapper(
    private val clock: Clock,
    private val context: Context,
) {
    fun mapToDisplayModel(race: Race): RaceDisplayModel {
        val countdownText = RaceCountdownFormatter.formatCountdown(race.advertisedStart, clock)
        val isLive = RaceCountdownFormatter.isRaceLive(race.advertisedStart, clock)
        
        val categoryName = getCategoryName(race.categoryId)
        val contentDescription = buildContentDescription(
            categoryName = categoryName,
            raceNumber = race.number,
            raceName = race.meetingName,
            isLive = isLive,
            countdownText = countdownText,
        )
        
        return RaceDisplayModel(
            id = race.id,
            raceName = race.meetingName,
            raceNumber = race.number,
            runnerName = "Next Runner",
            runnerNumber = 1,
            countdownText = countdownText,
            categoryColor = race.categoryId.categoryColor,
            categoryId = race.categoryId,
            isLive = isLive,
            contentDescription = contentDescription,
        )
    }

    private fun getCategoryName(categoryId: CategoryId): String {
        val stringRes = when (categoryId) {
            CategoryId.HORSE -> R.string.category_horse_racing
            CategoryId.GREYHOUND -> R.string.category_greyhound_racing
            CategoryId.HARNESS -> R.string.category_harness_racing
        }
        return context.getString(stringRes)
    }

    private fun buildContentDescription(
        categoryName: String,
        raceNumber: Int,
        raceName: String,
        isLive: Boolean,
        countdownText: String,
    ): String {
        return buildString {
            append("$categoryName race ")
            append("number $raceNumber ")
            append("at $raceName. ")
            if (isLive) {
                append("Race is currently live.")
            } else {
                append("Starting in $countdownText.")
            }
        }
    }
}