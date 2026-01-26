package ru.diaries.mydiaries.data.local.mapper

import ru.diaries.mydiaries.data.local.entity.FoodEntity
import ru.diaries.mydiaries.feature.food.data.model.FoodEntry
import ru.diaries.mydiaries.feature.food.data.model.ServingSize
import java.time.LocalDate

fun FoodEntity.toDomain(): FoodEntry {
    return FoodEntry(
        id = id,
        photoUri = photoUri,
        foodName = foodName,
        displayName = displayName,
        calories = calories,
        servingSize = ServingSize.fromString(servingSize),
        confidence = confidence,
        date = LocalDate.parse(date),
        createdAt = createdAt
    )
}

fun FoodEntry.toEntity(): FoodEntity {
    return FoodEntity(
        id = id,
        photoUri = photoUri,
        foodName = foodName,
        displayName = displayName,
        calories = calories,
        servingSize = servingSize.name,
        confidence = confidence,
        date = date.toString(),
        createdAt = createdAt
    )
}

fun List<FoodEntity>.toFoodDomainList(): List<FoodEntry> = map { it.toDomain() }
