package ru.diaries.mydiaries.feature.food.data.repository

import kotlinx.coroutines.flow.Flow
import ru.diaries.mydiaries.feature.food.data.model.FoodEntry

/**
 * Repository interface for food entry operations.
 */
interface FoodRepository {

    /**
     * Get all food entries as a Flow.
     */
    fun getAllFoodEntries(): Flow<List<FoodEntry>>

    /**
     * Save a new food entry.
     */
    suspend fun saveFoodEntry(entry: FoodEntry)

    /**
     * Delete a food entry by ID.
     */
    suspend fun deleteFoodEntry(id: String)
}
