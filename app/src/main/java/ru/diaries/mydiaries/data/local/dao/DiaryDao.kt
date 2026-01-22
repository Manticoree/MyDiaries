package ru.diaries.mydiaries.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.diaries.mydiaries.data.local.entity.DiaryEntryEntity

@Dao
interface DiaryDao {

    @Query("SELECT * FROM diary_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<DiaryEntryEntity>>

    @Query("SELECT * FROM diary_entries WHERE id = :id")
    suspend fun getEntryById(id: String): DiaryEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntryEntity)

    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun deleteEntry(id: String)
}
