package ru.diaries.mydiaries.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.diaries.mydiaries.data.local.entity.FoodEntity

@Dao
interface FoodDao {

    @Query("SELECT * FROM food_entries ORDER BY createdAt DESC")
    fun getAllFoodEntries(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM food_entries WHERE date = :date ORDER BY createdAt DESC")
    fun getFoodEntriesByDate(date: String): Flow<List<FoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(entry: FoodEntity)

    @Query("DELETE FROM food_entries WHERE id = :id")
    suspend fun deleteFoodEntry(id: String)
}
