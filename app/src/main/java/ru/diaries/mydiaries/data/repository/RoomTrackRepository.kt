package ru.diaries.mydiaries.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.diaries.mydiaries.data.local.calculator.TrackStatsCalculator
import ru.diaries.mydiaries.data.local.dao.TrackDao
import ru.diaries.mydiaries.data.local.entity.DailyTrackEntity
import ru.diaries.mydiaries.data.local.mapper.toDomain
import ru.diaries.mydiaries.data.local.mapper.toEntity
import ru.diaries.mydiaries.feature.track.data.model.DailyTrack
import ru.diaries.mydiaries.feature.track.data.model.LocationPoint
import ru.diaries.mydiaries.feature.track.data.repository.TrackRepository
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomTrackRepository @Inject constructor(
    private val trackDao: TrackDao
) : TrackRepository {

    override fun getAllTracks(): Flow<List<DailyTrack>> {
        return trackDao.getAllTracks().map { tracks ->
            tracks.map { entity ->
                val points = trackDao.getPointsForTrack(entity.id)
                entity.toDomain(points)
            }
        }
    }

    override fun getTrackByDate(date: LocalDate): Flow<DailyTrack?> {
        return trackDao.observeTrackByDate(date.toString()).map { entity ->
            entity?.let {
                val points = trackDao.getPointsForTrack(it.id)
                it.toDomain(points)
            }
        }
    }

    override suspend fun getDailyTrack(date: LocalDate): DailyTrack? {
        val dateStr = date.toString()
        val entity = trackDao.getTrackByDate(dateStr) ?: return null
        val points = trackDao.getPointsForTrack(entity.id)
        return entity.toDomain(points)
    }

    override suspend fun addLocationPoint(date: LocalDate, point: LocationPoint) {
        val dateStr = date.toString()
        val existingTrack = trackDao.getTrackByDate(dateStr)

        val trackId = if (existingTrack == null) {
            val newId = UUID.randomUUID().toString()
            trackDao.insertTrack(
                DailyTrackEntity(
                    id = newId,
                    date = dateStr,
                    distanceMeters = 0.0,
                    durationMillis = 0L,
                    averageSpeedKmh = 0.0,
                    startTime = point.timestamp,
                    endTime = point.timestamp,
                    isTracking = true
                )
            )
            newId
        } else {
            existingTrack.id
        }

        trackDao.insertPoint(point.toEntity(trackId))

        val allPoints = trackDao.getPointsForTrack(trackId)
        val distance = TrackStatsCalculator.calculateTotalDistance(allPoints)
        val duration = TrackStatsCalculator.calculateDuration(allPoints)
        val avgSpeed = TrackStatsCalculator.calculateAverageSpeed(distance, duration)

        val track = trackDao.getTrackByDate(dateStr)
        if (track != null) {
            trackDao.updateTrack(
                track.copy(
                    distanceMeters = distance,
                    durationMillis = duration,
                    averageSpeedKmh = avgSpeed,
                    endTime = point.timestamp
                )
            )
        }
    }

    override suspend fun finalizeTrack(date: LocalDate) {
        val track = trackDao.getTrackByDate(date.toString())
        if (track != null) {
            trackDao.updateTrack(track.copy(isTracking = false))
        }
    }

    override suspend fun deleteTrack(id: String) {
        trackDao.deleteTrack(id)
    }

    override suspend fun updateSteps(date: LocalDate, steps: Int) {
        val dateStr = date.toString()
        val existingTrack = trackDao.getTrackByDate(dateStr)
        if (existingTrack != null) {
            trackDao.updateStepsByDate(dateStr, steps)
        } else {
            trackDao.insertTrack(
                DailyTrackEntity(
                    id = UUID.randomUUID().toString(),
                    date = dateStr,
                    distanceMeters = 0.0,
                    durationMillis = 0L,
                    averageSpeedKmh = 0.0,
                    startTime = System.currentTimeMillis(),
                    endTime = System.currentTimeMillis(),
                    isTracking = false,
                    steps = steps
                )
            )
        }
    }
}
