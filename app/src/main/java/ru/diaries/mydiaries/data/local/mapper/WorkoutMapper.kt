package ru.diaries.mydiaries.data.local.mapper

import ru.diaries.mydiaries.data.local.entity.ExerciseEntity
import ru.diaries.mydiaries.data.local.entity.ExerciseSetEntity
import ru.diaries.mydiaries.data.local.entity.WorkoutEntity
import ru.diaries.mydiaries.feature.workout.data.model.Exercise
import ru.diaries.mydiaries.feature.workout.data.model.ExerciseSet
import ru.diaries.mydiaries.feature.workout.data.model.ExerciseType
import ru.diaries.mydiaries.feature.workout.data.model.Workout
import ru.diaries.mydiaries.feature.workout.data.model.WorkoutType
import java.time.LocalDate

fun WorkoutEntity.toDomain(
    exercises: List<Exercise> = emptyList()
): Workout {
    return Workout(
        id = id,
        name = name,
        date = LocalDate.parse(date),
        type = WorkoutType.entries.find { it.name == type } ?: WorkoutType.STRENGTH,
        durationMinutes = durationMinutes,
        programId = programId,
        notes = notes,
        exercises = exercises,
        createdAt = createdAt
    )
}

fun Workout.toEntity(): WorkoutEntity {
    return WorkoutEntity(
        id = id,
        name = name,
        date = date.toString(),
        type = type.name,
        durationMinutes = durationMinutes,
        programId = programId,
        notes = notes,
        createdAt = createdAt
    )
}

fun ExerciseEntity.toDomain(
    sets: List<ExerciseSet> = emptyList()
): Exercise {
    return Exercise(
        id = id,
        workoutId = workoutId,
        name = name,
        type = ExerciseType.entries.find { it.name == type } ?: ExerciseType.STRENGTH,
        order = order,
        sets = sets
    )
}

fun Exercise.toEntity(): ExerciseEntity {
    return ExerciseEntity(
        id = id,
        workoutId = workoutId,
        name = name,
        type = type.name,
        order = order
    )
}

fun ExerciseSetEntity.toDomain(): ExerciseSet {
    return ExerciseSet(
        id = id,
        exerciseId = exerciseId,
        setNumber = setNumber,
        reps = reps,
        weight = weight,
        durationSeconds = durationSeconds,
        distanceKm = distanceKm,
        isCompleted = isCompleted
    )
}

fun ExerciseSet.toEntity(): ExerciseSetEntity {
    return ExerciseSetEntity(
        id = id,
        exerciseId = exerciseId,
        setNumber = setNumber,
        reps = reps,
        weight = weight,
        durationSeconds = durationSeconds,
        distanceKm = distanceKm,
        isCompleted = isCompleted
    )
}
