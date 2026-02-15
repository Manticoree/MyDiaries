package ru.diaries.mydiaries.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.diaries.mydiaries.data.local.dao.WorkoutDao
import ru.diaries.mydiaries.data.local.mapper.toDomain
import ru.diaries.mydiaries.data.local.mapper.toEntity
import ru.diaries.mydiaries.feature.workout.data.model.Workout
import ru.diaries.mydiaries.feature.workout.data.repository.WorkoutRepository
import javax.inject.Inject

class RoomWorkoutRepository @Inject constructor(
    private val workoutDao: WorkoutDao
) : WorkoutRepository {

    override fun getAllWorkouts(): Flow<List<Workout>> {
        return workoutDao.getAllWorkouts().map { workoutEntities ->
            if (workoutEntities.isEmpty()) return@map emptyList()

            val workoutIds = workoutEntities.map { it.id }
            val exerciseEntities = workoutDao.getExercisesForWorkouts(workoutIds)
            val exerciseIds = exerciseEntities.map { it.id }
            val setEntities = if (exerciseIds.isNotEmpty()) {
                workoutDao.getSetsForExercises(exerciseIds)
            } else {
                emptyList()
            }

            val setsByExercise = setEntities.groupBy { it.exerciseId }
            val exercisesByWorkout = exerciseEntities.groupBy { it.workoutId }

            workoutEntities.map { workoutEntity ->
                val exercises = exercisesByWorkout[workoutEntity.id]?.map { exerciseEntity ->
                    val sets = setsByExercise[exerciseEntity.id]?.map { it.toDomain() } ?: emptyList()
                    exerciseEntity.toDomain(sets)
                } ?: emptyList()
                workoutEntity.toDomain(exercises)
            }
        }
    }

    override suspend fun saveWorkout(workout: Workout) {
        val workoutEntity = workout.toEntity()
        val exerciseEntities = workout.exercises.map { it.toEntity() }
        val setEntities = workout.exercises.flatMap { exercise ->
            exercise.sets.map { it.toEntity() }
        }
        workoutDao.insertWorkoutWithExercises(workoutEntity, exerciseEntities, setEntities)
    }

    override suspend fun deleteWorkout(id: String) {
        workoutDao.deleteWorkout(id)
    }
}
