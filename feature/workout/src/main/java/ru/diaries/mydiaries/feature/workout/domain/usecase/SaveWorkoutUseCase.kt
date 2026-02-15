package ru.diaries.mydiaries.feature.workout.domain.usecase

import ru.diaries.mydiaries.feature.workout.data.model.Workout
import ru.diaries.mydiaries.feature.workout.data.repository.WorkoutRepository
import javax.inject.Inject

class SaveWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    suspend operator fun invoke(workout: Workout): Result<Unit> {
        if (workout.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Workout name is required"))
        }

        return try {
            workoutRepository.saveWorkout(workout)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
