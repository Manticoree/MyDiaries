package ru.diaries.mydiaries.feature.track.data.repository

import kotlinx.coroutines.flow.Flow
import ru.diaries.mydiaries.feature.track.data.model.DailyTrack
import ru.diaries.mydiaries.feature.track.data.model.LocationPoint
import java.time.LocalDate

interface TrackRepository {
    fun getAllTracks(): Flow<List<DailyTrack>>
    fun getTrackByDate(date: LocalDate): Flow<DailyTrack?>
    suspend fun addLocationPoint(date: LocalDate, point: LocationPoint)
    suspend fun finalizeTrack(date: LocalDate)
    suspend fun deleteTrack(id: String)
    suspend fun updateSteps(date: LocalDate, steps: Int)
}
