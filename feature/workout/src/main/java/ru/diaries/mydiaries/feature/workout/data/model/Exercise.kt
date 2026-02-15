package ru.diaries.mydiaries.feature.workout.data.model

data class Exercise(
    val id: String,
    val workoutId: String,
    val name: String,
    val type: ExerciseType = ExerciseType.STRENGTH,
    val order: Int = 0,
    val sets: List<ExerciseSet> = emptyList()
)

enum class ExerciseType {
    STRENGTH,
    CARDIO
}
