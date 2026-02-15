package ru.diaries.mydiaries.feature.workout.data.model

data class ExerciseSet(
    val id: String,
    val exerciseId: String,
    val setNumber: Int = 1,
    val reps: Int = 0,
    val weight: Double = 0.0,
    val durationSeconds: Int = 0,
    val distanceKm: Double = 0.0,
    val isCompleted: Boolean = false
)
