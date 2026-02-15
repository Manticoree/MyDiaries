package ru.diaries.mydiaries.feature.workout.ui.list

import ru.diaries.mydiaries.feature.workout.data.model.Workout
import ru.diaries.mydiaries.feature.workout.data.model.WorkoutProgram
import ru.diaries.mydiaries.feature.workout.data.model.WorkoutType

data class WorkoutListState(
    val workouts: List<Workout> = emptyList(),
    val programs: List<WorkoutProgram> = emptyList(),
    val selectedFilter: WorkoutTypeFilter = WorkoutTypeFilter.ALL,
    val isLoading: Boolean = true,
    val error: String? = null,
    val showAddWorkoutDialog: Boolean = false,
    val addWorkoutDialogKey: String = "",
    val selectedProgramId: String? = null,
    val activeWorkout: Workout? = null,
    val isGenerating: Boolean = false,
    val generatedWorkout: Workout? = null,
    val generationError: String? = null
) {
    val filteredWorkouts: List<Workout>
        get() = when (selectedFilter) {
            WorkoutTypeFilter.ALL -> workouts
            WorkoutTypeFilter.STRENGTH -> workouts.filter { it.type == WorkoutType.STRENGTH }
            WorkoutTypeFilter.CARDIO -> workouts.filter { it.type == WorkoutType.CARDIO }
            WorkoutTypeFilter.MIXED -> workouts.filter { it.type == WorkoutType.MIXED }
        }

    val totalWorkouts: Int get() = workouts.size

    val averageDuration: Int
        get() {
            val withDuration = workouts.filter { it.durationMinutes > 0 }
            return if (withDuration.isNotEmpty()) {
                withDuration.sumOf { it.durationMinutes } / withDuration.size
            } else 0
        }

    val thisWeekCount: Int
        get() {
            val now = java.time.LocalDate.now()
            val weekStart = now.minusDays(now.dayOfWeek.value.toLong() - 1)
            return workouts.count { it.date >= weekStart }
        }
}

enum class WorkoutTypeFilter {
    ALL, STRENGTH, CARDIO, MIXED
}
