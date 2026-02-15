package ru.diaries.mydiaries.feature.workout.ui

import ru.diaries.mydiaries.feature.workout.data.model.ExerciseType
import ru.diaries.mydiaries.feature.workout.data.model.WorkoutProgram
import ru.diaries.mydiaries.feature.workout.data.model.WorkoutType

data class AddWorkoutDialogState(
    val name: String = "",
    val workoutType: WorkoutType = WorkoutType.STRENGTH,
    val durationMinutes: String = "",
    val notes: String = "",
    val selectedProgramId: String? = null,
    val selectedDayIndex: Int = 0,
    val programs: List<WorkoutProgram> = emptyList(),
    val exercises: List<DialogExercise> = emptyList(),
    val isSaving: Boolean = false,
    val error: String? = null
)

data class DialogExercise(
    val name: String = "",
    val type: ExerciseType = ExerciseType.STRENGTH,
    val sets: Int = 3,
    val reps: Int = 10,
    val weight: Double = 0.0,
    val durationSeconds: Int = 0,
    val distanceKm: Double = 0.0
)

sealed class AddWorkoutDialogIntent {
    data class NameChanged(val name: String) : AddWorkoutDialogIntent()
    data class TypeChanged(val type: WorkoutType) : AddWorkoutDialogIntent()
    data class DurationChanged(val duration: String) : AddWorkoutDialogIntent()
    data class NotesChanged(val notes: String) : AddWorkoutDialogIntent()
    data class ProgramSelected(val programId: String?) : AddWorkoutDialogIntent()
    data class DaySelected(val dayIndex: Int) : AddWorkoutDialogIntent()
    data object AddExercise : AddWorkoutDialogIntent()
    data class RemoveExercise(val index: Int) : AddWorkoutDialogIntent()
    data class ExerciseNameChanged(val index: Int, val name: String) : AddWorkoutDialogIntent()
    data class ExerciseSetsChanged(val index: Int, val sets: String) : AddWorkoutDialogIntent()
    data class ExerciseRepsChanged(val index: Int, val reps: String) : AddWorkoutDialogIntent()
    data class ExerciseWeightChanged(val index: Int, val weight: String) : AddWorkoutDialogIntent()
    data class ExerciseDurationChanged(val index: Int, val duration: String) : AddWorkoutDialogIntent()
    data class ExerciseDistanceChanged(val index: Int, val distance: String) : AddWorkoutDialogIntent()
    data class ExerciseTypeChanged(val index: Int, val type: ExerciseType) : AddWorkoutDialogIntent()
    data object Save : AddWorkoutDialogIntent()
    data object ClearError : AddWorkoutDialogIntent()
}

sealed class AddWorkoutDialogEffect {
    data object SaveSuccess : AddWorkoutDialogEffect()
}
