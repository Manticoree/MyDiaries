package ru.diaries.mydiaries.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.diaries.mydiaries.data.local.entity.DailyTrackEntity
import ru.diaries.mydiaries.data.local.entity.LocationPointEntity

@Dao
interface TrackDao {

    @Query("SELECT * FROM daily_tracks ORDER BY date DESC")
    fun getAllTracks(): Flow<List<DailyTrackEntity>>

    @Query("SELECT * FROM daily_tracks WHERE date = :date LIMIT 1")
    suspend fun getTrackByDate(date: String): DailyTrackEntity?

    @Query("SELECT * FROM daily_tracks WHERE date = :date LIMIT 1")
    fun observeTrackByDate(date: String): Flow<DailyTrackEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: DailyTrackEntity)

    @Update
    suspend fun updateTrack(track: DailyTrackEntity)

    @Query("DELETE FROM daily_tracks WHERE id = :id")
    suspend fun deleteTrack(id: String)

    @Query("SELECT * FROM location_points WHERE trackId = :trackId ORDER BY timestamp ASC")
    suspend fun getPointsForTrack(trackId: String): List<LocationPointEntity>

    @Query("SELECT * FROM location_points WHERE trackId = :trackId ORDER BY timestamp ASC")
    fun observePointsForTrack(trackId: String): Flow<List<LocationPointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(point: LocationPointEntity)

    @Query("UPDATE daily_tracks SET steps = :steps WHERE date = :date")
    suspend fun updateStepsByDate(date: String, steps: Int)
}
