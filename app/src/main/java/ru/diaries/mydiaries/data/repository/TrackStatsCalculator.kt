package ru.diaries.mydiaries.data.repository

import ru.diaries.mydiaries.data.local.entity.LocationPointEntity
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object TrackStatsCalculator {

    fun calculateTotalDistance(points: List<LocationPointEntity>): Double {
        if (points.size < 2) return 0.0
        var total = 0.0
        for (i in 0 until points.size - 1) {
            total += haversineDistance(
                points[i].latitude, points[i].longitude,
                points[i + 1].latitude, points[i + 1].longitude
            )
        }
        return total
    }

    fun calculateDuration(points: List<LocationPointEntity>): Long {
        if (points.size < 2) return 0L
        return points.last().timestamp - points.first().timestamp
    }

    fun calculateAverageSpeed(distanceMeters: Double, durationMillis: Long): Double {
        if (durationMillis <= 0) return 0.0
        val hours = durationMillis / 3_600_000.0
        val km = distanceMeters / 1000.0
        return km / hours
    }

    private fun haversineDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadiusMeters = 6_371_000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * asin(sqrt(a))
        return earthRadiusMeters * c
    }
}
