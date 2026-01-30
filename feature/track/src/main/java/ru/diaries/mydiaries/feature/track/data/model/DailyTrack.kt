package ru.diaries.mydiaries.feature.track.data.model

import java.time.LocalDate

data class DailyTrack(
    val id: String,
    val date: LocalDate,
    val points: List<LocationPoint>,
    val distanceMeters: Double,
    val durationMillis: Long,
    val averageSpeedKmh: Double,
    val startTime: Long,
    val endTime: Long,
    val isTracking: Boolean,
    val steps: Int = 0
)
