package ru.diaries.mydiaries.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.diaries.mydiaries.data.local.entity.PhotoEntity

@Dao
interface PhotoDao {

    @Query("SELECT * FROM photos WHERE entryId = :entryId ORDER BY position ASC")
    suspend fun getPhotosForEntrySync(entryId: String): List<PhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>)

    @Query("DELETE FROM photos WHERE entryId = :entryId")
    suspend fun deletePhotosForEntry(entryId: String)
}
