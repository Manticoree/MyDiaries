package ru.diaries.mydiaries.feature.workout.ui.browser

import ru.diaries.mydiaries.feature.workout.data.api.WgerCategory
import ru.diaries.mydiaries.feature.workout.data.api.WgerExerciseInfo

data class ExerciseBrowserState(
    val categories: List<WgerCategory> = emptyList(),
    val exercises: List<WgerExerciseInfo> = emptyList(),
    val selectedCategoryId: Int? = null,
    val isLoadingCategories: Boolean = false,
    val isLoadingExercises: Boolean = false,
    val isLoadingMore: Boolean = false,
    val searchQuery: String = "",
    val isSearchMode: Boolean = false,
    val error: String? = null,
    val currentOffset: Int = 0,
    val hasMore: Boolean = true,
    val selectedExerciseId: Int? = null
)
