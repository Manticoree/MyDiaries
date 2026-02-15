package ru.diaries.mydiaries.feature.track.data

import ru.diaries.mydiaries.feature.track.data.model.LocationPoint
import ru.diaries.mydiaries.feature.track.data.model.PlaceType
import ru.diaries.mydiaries.feature.track.data.model.VisitedPlace
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object StopDetector {

    private const val STOP_RADIUS_METERS = 50.0
    private const val MIN_STOP_DURATION_MS = 2 * 60 * 1000L

    fun detectPlaces(points: List<LocationPoint>): List<VisitedPlace> {
        if (points.isEmpty()) return emptyList()

        val places = mutableListOf<VisitedPlace>()

        val first = points.first()
        val last = points.last()

        if (points.size == 1) {
            return listOf(
                VisitedPlace(
                    latitude = first.latitude,
                    longitude = first.longitude,
                    name = null,
                    arrivalTime = first.timestamp,
                    departureTime = first.timestamp,
                    type = PlaceType.START
                )
            )
        }

        places.add(
            VisitedPlace(
                latitude = first.latitude,
                longitude = first.longitude,
                name = null,
                arrivalTime = first.timestamp,
                departureTime = first.timestamp,
                type = PlaceType.START
            )
        )

        val stops = detectStops(points)
        places.addAll(stops)

        places.add(
            VisitedPlace(
                latitude = last.latitude,
                longitude = last.longitude,
                name = null,
                arrivalTime = last.timestamp,
                departureTime = last.timestamp,
                type = PlaceType.END
            )
        )

        return places
    }

    private fun detectStops(points: List<LocationPoint>): List<VisitedPlace> {
        if (points.size < 3) return emptyList()

        val stops = mutableListOf<VisitedPlace>()
        var clusterStart = 1
        val lastIndex = points.size - 2

        while (clusterStart <= lastIndex) {
            val anchor = points[clusterStart]
            var clusterEnd = clusterStart

            for (i in clusterStart + 1..points.size - 2) {
                val dist = haversineDistance(
                    anchor.latitude, anchor.longitude,
                    points[i].latitude, points[i].longitude
                )
                if (dist > STOP_RADIUS_METERS) break
                clusterEnd = i
            }

            val duration = points[clusterEnd].timestamp - points[clusterStart].timestamp
            if (duration >= MIN_STOP_DURATION_MS && clusterEnd > clusterStart) {
                var sumLat = 0.0
                var sumLon = 0.0
                val count = clusterEnd - clusterStart + 1
                for (i in clusterStart..clusterEnd) {
                    sumLat += points[i].latitude
                    sumLon += points[i].longitude
                }
                stops.add(
                    VisitedPlace(
                        latitude = sumLat / count,
                        longitude = sumLon / count,
                        name = null,
                        arrivalTime = points[clusterStart].timestamp,
                        departureTime = points[clusterEnd].timestamp,
                        type = PlaceType.STOP
                    )
                )
                clusterStart = clusterEnd + 1
            } else {
                clusterStart++
            }
        }

        return stops
    }

    private fun haversineDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val r = 6_371_000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
