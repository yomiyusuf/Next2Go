package com.yomi.next2go.core.domain.mapper

import com.yomi.next2go.core.common.time.Clock
import com.yomi.next2go.core.domain.model.CategoryId
import com.yomi.next2go.core.domain.model.CategoryColor
import com.yomi.next2go.core.domain.model.Race
import com.yomi.next2go.core.domain.model.RaceDisplayModel
import com.yomi.next2go.core.domain.formatter.RaceCountdownFormatter

class RaceDisplayModelMapper(
    private val clock: Clock
) {
    fun mapToDisplayModel(race: Race): RaceDisplayModel {
        return RaceDisplayModel(
            id = race.id,
            raceName = "${race.meetingName} R${race.number}",
            raceNumber = race.number,
            runnerName = "Next Runner",
            runnerNumber = 1,
            jockeyName = "TBA",
            bestTime = "--:--",
            odds = "--",
            countdownText = RaceCountdownFormatter.formatCountdown(race.advertisedStart, clock),
            categoryColor = getCategoryColor(race.categoryId),
            isLive = RaceCountdownFormatter.isRaceLive(race.advertisedStart, clock)
        )
    }

    private fun getCategoryColor(categoryId: CategoryId): CategoryColor {
        return when (categoryId) {
            CategoryId.HORSE -> CategoryColor.GREEN
            CategoryId.GREYHOUND -> CategoryColor.RED
            CategoryId.HARNESS -> CategoryColor.YELLOW
        }
    }
}