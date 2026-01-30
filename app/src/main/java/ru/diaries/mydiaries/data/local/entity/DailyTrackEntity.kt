package ru.diaries.mydiaries.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_tracks")
data class DailyTrackEntity(
    @PrimaryKey
    val id: String,
    val date: String,
    val distanceMeters: Double,
    val durationMillis: Long,
    val averageSpeedKmh: Double,
    val startTime: Long,
    val endTime: Long,
    val isTracking: Boolean,
    val steps: Int = 0
)
