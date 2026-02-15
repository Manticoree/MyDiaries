package ru.diaries.mydiaries.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val date: String,
    val type: String,
    val durationMinutes: Int = 0,
    val programId: String? = null,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
