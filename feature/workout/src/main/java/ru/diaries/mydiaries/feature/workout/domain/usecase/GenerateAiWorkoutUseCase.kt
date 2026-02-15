package ru.diaries.mydiaries.feature.workout.domain.usecase

import kotlinx.coroutines.flow.first
import ru.diaries.mydiaries.feature.workout.data.ai.AiWorkoutGenerator
import ru.diaries.mydiaries.feature.workout.data.model.Workout
import ru.diaries.mydiaries.feature.workout.data.repository.WorkoutRepository
import javax.inject.Inject

class GenerateAiWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val aiWorkoutGenerator: AiWorkoutGenerator
) {
    suspend operator fun invoke(): Result<Workout> {
        return try {
            val history = workoutRepository.getAllWorkouts().first()
            aiWorkoutGenerator.generateWorkout(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
