package ru.diaries.mydiaries.feature.workout.data.model

data class WorkoutProgram(
    val id: String,
    val name: String,
    val description: String,
    val type: WorkoutType,
    val days: List<ProgramDay> = emptyList()
)

data class ProgramDay(
    val name: String,
    val templateExercises: List<TemplateExercise> = emptyList()
)

data class TemplateExercise(
    val name: String,
    val type: ExerciseType = ExerciseType.STRENGTH,
    val defaultSets: Int = 3,
    val defaultReps: Int = 10,
    val defaultDurationSeconds: Int = 0,
    val defaultDistanceKm: Double = 0.0
)
