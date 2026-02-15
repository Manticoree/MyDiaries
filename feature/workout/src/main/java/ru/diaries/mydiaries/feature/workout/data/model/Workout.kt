package ru.diaries.mydiaries.feature.workout.data.model

import java.time.LocalDate

data class Workout(
    val id: String,
    val name: String,
    val date: LocalDate = LocalDate.now(),
    val type: WorkoutType = WorkoutType.STRENGTH,
    val durationMinutes: Int = 0,
    val programId: String? = null,
    val notes: String = "",
    val exercises: List<Exercise> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class WorkoutType {
    STRENGTH,
    CARDIO,
    MIXED
}
