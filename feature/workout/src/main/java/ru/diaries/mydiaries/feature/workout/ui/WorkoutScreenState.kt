package ru.diaries.mydiaries.feature.workout.ui

import ru.diaries.mydiaries.feature.workout.data.model.Workout

data class WorkoutScreenState(
    val workout: Workout? = null,
    val isLoading: Boolean = true,
    val restTimerSeconds: Int = 0,
    val isRestTimerRunning: Boolean = false,
    val elapsedSeconds: Long = 0,
    val isTimerRunning: Boolean = false,
    val error: String? = null
)

sealed class WorkoutScreenIntent {
    data class LoadWorkout(val workout: Workout) : WorkoutScreenIntent()
    data class ToggleSetCompletion(val exerciseId: String, val setId: String) : WorkoutScreenIntent()
    data class UpdateSetReps(val setId: String, val reps: Int) : WorkoutScreenIntent()
    data class UpdateSetWeight(val setId: String, val weight: Double) : WorkoutScreenIntent()
    data object StartRestTimer : WorkoutScreenIntent()
    data object StopRestTimer : WorkoutScreenIntent()
    data object ToggleWorkoutTimer : WorkoutScreenIntent()
    data object FinishWorkout : WorkoutScreenIntent()
}

sealed class WorkoutScreenEffect {
    data object WorkoutFinished : WorkoutScreenEffect()
}
