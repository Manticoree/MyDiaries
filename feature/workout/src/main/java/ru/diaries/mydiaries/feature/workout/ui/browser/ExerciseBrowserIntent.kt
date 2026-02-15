package ru.diaries.mydiaries.feature.workout.ui.browser

sealed class ExerciseBrowserIntent {
    data object LoadCategories : ExerciseBrowserIntent()
    data class SelectCategory(val categoryId: Int?) : ExerciseBrowserIntent()
    data class SearchQueryChanged(val query: String) : ExerciseBrowserIntent()
    data object Search : ExerciseBrowserIntent()
    data object LoadMore : ExerciseBrowserIntent()
    data object ClearSearch : ExerciseBrowserIntent()
    data class SelectExercise(val exerciseId: Int) : ExerciseBrowserIntent()
    data object DismissExerciseDetail : ExerciseBrowserIntent()
}
