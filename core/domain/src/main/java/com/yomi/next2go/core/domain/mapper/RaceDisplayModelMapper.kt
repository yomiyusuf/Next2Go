package com.yomi.next2go.core.domain.mapper

import com.yomi.next2go.core.common.time.Clock
import com.yomi.next2go.core.domain.formatter.RaceCountdownFormatter
import com.yomi.next2go.core.domain.model.CategoryColor
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.Race
import com.yomi.next2go.core.domain.model.RaceDisplayModel

class RaceDisplayModelMapper(
    private val clock: Clock,
) {
    fun mapToDisplayModel(race: Race): RaceDisplayModel {
        val countdownText = RaceCountdownFormatter.formatCountdown(race.advertisedStart, clock)
        val isLive = RaceCountdownFormatter.isRaceLive(race.advertisedStart, clock)
        
        return RaceDisplayModel(
            id = race.id,
            raceName = race.meetingName,
            raceNumber = race.number,
            runnerName = "Next Runner",
            runnerNumber = 1,
            countdownText = countdownText,
            categoryColor = getCategoryColor(race.categoryId),
            categoryId = race.categoryId,
            isLive = isLive,
            contentDescription = buildContentDescription(
                categoryId = race.categoryId,
                raceNumber = race.number,
                meetingName = race.meetingName,
                countdownText = countdownText,
                isLive = isLive
            ),
        )
    }

    private fun getCategoryColor(categoryId: CategoryId): CategoryColor {
        return when (categoryId) {
            CategoryId.HORSE -> CategoryColor.GREEN
            CategoryId.GREYHOUND -> CategoryColor.RED
            CategoryId.HARNESS -> CategoryColor.YELLOW
        }
    }
    
    private fun buildContentDescription(
        categoryId: CategoryId,
        raceNumber: Int,
        meetingName: String,
        countdownText: String,
        isLive: Boolean
    ): String {
        val categoryName = when (categoryId) {
            CategoryId.HORSE -> "Horse Racing"
            CategoryId.GREYHOUND -> "Greyhound Racing"
            CategoryId.HARNESS -> "Harness Racing"
        }
        
        return buildString {
            append("$categoryName race ")
            append("number $raceNumber ")
            append("at $meetingName. ")
            if (isLive) {
                append("Race is currently live.")
            } else {
                append("Starting in $countdownText.")
            }
        }
    }
}
