package ru.diaries.mydiaries.feature.food.data.model

import java.time.LocalDate

/**
 * Domain model for a food entry.
 */
data class FoodEntry(
    val id: String,
    val photoUri: String,
    val foodName: String,
    val displayName: String,
    val calories: Int,
    val servingSize: ServingSize = ServingSize.MEDIUM,
    val confidence: Float,
    val date: LocalDate = LocalDate.now(),
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Calculate actual calories based on serving size.
     */
    val actualCalories: Int
        get() = (calories * servingSize.multiplier).toInt()
}

/**
 * Serving size options with calorie multipliers.
 */
enum class ServingSize(
    val multiplier: Float,
    val displayNameRu: String
) {
    SMALL(0.5f, "S"),
    MEDIUM(1.0f, "M"),
    LARGE(1.5f, "L"),
    EXTRA_LARGE(2.0f, "XL");

    companion object {
        fun fromString(value: String): ServingSize {
            return entries.find { it.name == value } ?: MEDIUM
        }
    }
}
