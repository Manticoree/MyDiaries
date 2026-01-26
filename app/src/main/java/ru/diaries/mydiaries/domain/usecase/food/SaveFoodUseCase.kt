package ru.diaries.mydiaries.domain.usecase.food

import ru.diaries.mydiaries.feature.food.data.model.FoodEntry
import ru.diaries.mydiaries.feature.food.data.repository.FoodRepository
import javax.inject.Inject

class SaveFoodUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    suspend operator fun invoke(entry: FoodEntry) {
        foodRepository.saveFoodEntry(entry)
    }
}
