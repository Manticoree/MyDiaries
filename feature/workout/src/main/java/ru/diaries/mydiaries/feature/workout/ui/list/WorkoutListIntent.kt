package ru.diaries.mydiaries.feature.workout.ui.list

import ru.diaries.mydiaries.feature.workout.data.model.Workout

sealed class WorkoutListIntent {
    data object LoadData : WorkoutListIntent()
    data class FilterByType(val filter: WorkoutTypeFilter) : WorkoutListIntent()
    data class DeleteWorkout(val id: String) : WorkoutListIntent()
    data class StartWorkout(val workout: Workout) : WorkoutListIntent()
    data object CloseWorkoutDetail : WorkoutListIntent()
    data object ShowAddWorkoutDialog : WorkoutListIntent()
    data class ShowAddWorkoutWithProgram(val programId: String) : WorkoutListIntent()
    data object HideAddWorkoutDialog : WorkoutListIntent()
    data object GenerateAiWorkout : WorkoutListIntent()
    data object AcceptGeneratedWorkout : WorkoutListIntent()
    data object DismissGeneratedWorkout : WorkoutListIntent()
}
