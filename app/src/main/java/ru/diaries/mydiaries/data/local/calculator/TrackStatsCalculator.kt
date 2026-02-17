package ru.diaries.mydiaries.data.local.calculator

import ru.diaries.mydiaries.data.local.entity.LocationPointEntity
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Calculator for track statistics like distance, duration, and average speed.
 */
object TrackStatsCalculator {

    /**
     * Calculates the total distance in meters using the Haversine formula.
     */
    fun calculateTotalDistance(points: List<LocationPointEntity>): Double {
        if (points.size < 2) return 0.0

        var totalDistance = 0.0
        for (i in 0 until points.size - 1) {
            val p1 = points[i]
            val p2 = points[i + 1]
            totalDistance += haversine(p1.latitude, p1.longitude, p2.latitude, p2.longitude)
        }
        return totalDistance
    }

    /**
     * Calculates the duration in milliseconds between the first and last point.
     */
    fun calculateDuration(points: List<LocationPointEntity>): Long {
        if (points.size < 2) return 0L
        val startTime = points.first().timestamp
        val endTime = points.last().timestamp
        return endTime - startTime
    }

    /**
     * Calculates the average speed in km/h.
     */
    fun calculateAverageSpeed(distanceMeters: Double, durationMillis: Long): Double {
        if (durationMillis == 0L) return 0.0
        val distanceKm = distanceMeters / 1000.0
        val durationHours = durationMillis / (1000.0 * 60.0 * 60.0)
        return if (durationHours > 0) distanceKm / durationHours else 0.0
    }

    /**
     * Haversine formula to calculate distance between two points on a sphere.
     * Returns distance in meters.
     */
    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // Earth's radius in meters

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = atan2(sin(dLat / 2), 2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                atan2(sin(dLon / 2), 2.0)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

    private fun sin(x: Double): Double = kotlin.math.sin(x)
    private fun cos(x: Double): Double = kotlin.math.cos(x)
    private fun sqrt(x: Double): Double = kotlin.math.sqrt(x)
    private fun atan2(y: Double, x: Double): Double = kotlin.math.atan2(y, x)
}
