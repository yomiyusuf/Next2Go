package com.yomi.next2go.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse(
    @Json(name = "status") val status: Int,
    @Json(name = "data") val data: RacingData,
    @Json(name = "message") val message: String,
)

@JsonClass(generateAdapter = true)
data class RacingData(
    @Json(name = "next_to_go_ids") val nextToGoIds: List<String>,
    @Json(name = "race_summaries") val raceSummaries: Map<String, RaceDto>,
)

@JsonClass(generateAdapter = true)
data class RaceDto(
    @Json(name = "race_id") val raceId: String,
    @Json(name = "race_name") val raceName: String,
    @Json(name = "race_number") val raceNumber: Int,
    @Json(name = "meeting_name") val meetingName: String,
    @Json(name = "category_id") val categoryId: String,
    @Json(name = "advertised_start") val advertisedStart: AdvertisedStartDto,
)

@JsonClass(generateAdapter = true)
data class AdvertisedStartDto(
    @Json(name = "seconds") val seconds: Long,
)