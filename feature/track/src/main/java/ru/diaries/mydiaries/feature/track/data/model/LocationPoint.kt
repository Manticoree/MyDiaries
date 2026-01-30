package ru.diaries.mydiaries.feature.track.data.model

data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Float,
    val accuracy: Float,
    val timestamp: Long
)
