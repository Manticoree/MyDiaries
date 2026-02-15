package ru.diaries.mydiaries.feature.workout.data.repository

import kotlinx.coroutines.flow.Flow
import ru.diaries.mydiaries.feature.workout.data.model.Workout

interface WorkoutRepository {

    fun getAllWorkouts(): Flow<List<Workout>>

    suspend fun saveWorkout(workout: Workout)

    suspend fun deleteWorkout(id: String)
}
