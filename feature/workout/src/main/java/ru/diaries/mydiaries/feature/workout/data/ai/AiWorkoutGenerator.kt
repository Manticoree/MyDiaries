package ru.diaries.mydiaries.feature.workout.data.ai

import ru.diaries.mydiaries.feature.workout.data.model.Workout

interface AiWorkoutGenerator {
    suspend fun generateWorkout(history: List<Workout>): Result<Workout>
}
