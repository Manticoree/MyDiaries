package ru.diaries.mydiaries.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.diaries.mydiaries.data.local.dao.FoodDao
import ru.diaries.mydiaries.data.local.mapper.toEntity
import ru.diaries.mydiaries.data.local.mapper.toFoodDomainList
import ru.diaries.mydiaries.feature.food.data.model.FoodEntry
import ru.diaries.mydiaries.feature.food.data.repository.FoodRepository
import javax.inject.Inject

class RoomFoodRepository @Inject constructor(
    private val foodDao: FoodDao
) : FoodRepository {

    override fun getAllFoodEntries(): Flow<List<FoodEntry>> {
        return foodDao.getAllFoodEntries().map { it.toFoodDomainList() }
    }

    override suspend fun saveFoodEntry(entry: FoodEntry) {
        foodDao.insertFoodEntry(entry.toEntity())
    }

    override suspend fun deleteFoodEntry(id: String) {
        foodDao.deleteFoodEntry(id)
    }
}
