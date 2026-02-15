package ru.diaries.mydiaries.feature.track.data.model

data class VisitedPlace(
    val latitude: Double,
    val longitude: Double,
    val name: String?,
    val arrivalTime: Long,
    val departureTime: Long,
    val type: PlaceType
)

enum class PlaceType { START, STOP, END }
