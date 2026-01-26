package ru.diaries.mydiaries.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_entries")
data class FoodEntity(
    @PrimaryKey
    val id: String,
    val photoUri: String,
    val foodName: String,
    val displayName: String,
    val calories: Int,
    val servingSize: String,
    val confidence: Float,
    val date: String, // ISO format: yyyy-MM-dd
    val createdAt: Long = System.currentTimeMillis()
)
