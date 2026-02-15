package ru.diaries.mydiaries.feature.workout.ui.browser

import ru.diaries.mydiaries.feature.workout.data.api.WgerExerciseInfo

data class ExerciseDetailState(
    val exercise: WgerExerciseInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
