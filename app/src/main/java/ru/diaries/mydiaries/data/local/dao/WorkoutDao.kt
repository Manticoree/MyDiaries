package ru.diaries.mydiaries.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.diaries.mydiaries.data.local.entity.ExerciseEntity
import ru.diaries.mydiaries.data.local.entity.ExerciseSetEntity
import ru.diaries.mydiaries.data.local.entity.WorkoutEntity

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workouts ORDER BY createdAt DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE date = :date ORDER BY createdAt DESC")
    fun getWorkoutsByDate(date: String): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY `order` ASC")
    suspend fun getExercisesForWorkout(workoutId: String): List<ExerciseEntity>

    @Query("SELECT * FROM exercise_sets WHERE exerciseId = :exerciseId ORDER BY setNumber ASC")
    suspend fun getSetsForExercise(exerciseId: String): List<ExerciseSetEntity>

    @Query("SELECT * FROM exercises WHERE workoutId IN (:workoutIds) ORDER BY `order` ASC")
    suspend fun getExercisesForWorkouts(workoutIds: List<String>): List<ExerciseEntity>

    @Query("SELECT * FROM exercise_sets WHERE exerciseId IN (:exerciseIds) ORDER BY setNumber ASC")
    suspend fun getSetsForExercises(exerciseIds: List<String>): List<ExerciseSetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseSet(set: ExerciseSetEntity)

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteWorkout(id: String)

    @Transaction
    suspend fun insertWorkoutWithExercises(
        workout: WorkoutEntity,
        exercises: List<ExerciseEntity>,
        sets: List<ExerciseSetEntity>
    ) {
        insertWorkout(workout)
        exercises.forEach { insertExercise(it) }
        sets.forEach { insertExerciseSet(it) }
    }
}
