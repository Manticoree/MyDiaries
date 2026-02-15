package ru.diaries.mydiaries.feature.workout.data.local

import ru.diaries.mydiaries.feature.workout.data.api.WgerCategory
import ru.diaries.mydiaries.feature.workout.data.api.WgerExerciseInfo

data class ExercisePage(
    val exercises: List<WgerExerciseInfo>,
    val hasMore: Boolean
)

interface ExerciseBrowserRepository {
    suspend fun getCategories(): List<WgerCategory>
    suspend fun getExercises(category: Int?, limit: Int, offset: Int): ExercisePage
    suspend fun searchExercises(term: String): List<WgerExerciseInfo>
    suspend fun getExerciseById(id: Int): WgerExerciseInfo?
}
