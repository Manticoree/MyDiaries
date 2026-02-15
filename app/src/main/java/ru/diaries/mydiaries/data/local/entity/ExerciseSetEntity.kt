package ru.diaries.mydiaries.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseId")]
)
data class ExerciseSetEntity(
    @PrimaryKey
    val id: String,
    val exerciseId: String,
    val setNumber: Int = 1,
    val reps: Int = 0,
    val weight: Double = 0.0,
    val durationSeconds: Int = 0,
    val distanceKm: Double = 0.0,
    val isCompleted: Boolean = false
)
