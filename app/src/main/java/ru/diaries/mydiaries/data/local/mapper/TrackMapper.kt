package ru.diaries.mydiaries.data.local.mapper

import ru.diaries.mydiaries.data.local.entity.DailyTrackEntity
import ru.diaries.mydiaries.data.local.entity.LocationPointEntity
import ru.diaries.mydiaries.feature.track.data.model.DailyTrack
import ru.diaries.mydiaries.feature.track.data.model.LocationPoint
import java.time.LocalDate

fun DailyTrackEntity.toDomain(points: List<LocationPointEntity>): DailyTrack {
    return DailyTrack(
        id = id,
        date = LocalDate.parse(date),
        points = points.map { it.toDomain() },
        distanceMeters = distanceMeters,
        durationMillis = durationMillis,
        averageSpeedKmh = averageSpeedKmh,
        startTime = startTime,
        endTime = endTime,
        isTracking = isTracking,
        steps = steps
    )
}

fun LocationPointEntity.toDomain(): LocationPoint {
    return LocationPoint(
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        speed = speed,
        accuracy = accuracy,
        timestamp = timestamp
    )
}

fun LocationPoint.toEntity(trackId: String): LocationPointEntity {
    return LocationPointEntity(
        trackId = trackId,
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        speed = speed,
        accuracy = accuracy,
        timestamp = timestamp
    )
}
