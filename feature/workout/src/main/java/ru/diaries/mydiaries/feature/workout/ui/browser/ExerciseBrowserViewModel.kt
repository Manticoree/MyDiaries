package ru.diaries.mydiaries.feature.workout.ui.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.feature.workout.data.local.ExerciseBrowserRepository
import javax.inject.Inject

@HiltViewModel
class ExerciseBrowserViewModel @Inject constructor(
    private val repository: ExerciseBrowserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExerciseBrowserState())
    val state: StateFlow<ExerciseBrowserState> = _state.asStateFlow()

    init {
        handleIntent(ExerciseBrowserIntent.LoadCategories)
    }

    fun handleIntent(intent: ExerciseBrowserIntent) {
        when (intent) {
            is ExerciseBrowserIntent.LoadCategories -> loadCategories()
            is ExerciseBrowserIntent.SelectCategory -> selectCategory(intent.categoryId)
            is ExerciseBrowserIntent.SearchQueryChanged -> updateSearchQuery(intent.query)
            is ExerciseBrowserIntent.Search -> performSearch()
            is ExerciseBrowserIntent.LoadMore -> loadMore()
            is ExerciseBrowserIntent.ClearSearch -> clearSearch()
            is ExerciseBrowserIntent.SelectExercise -> selectExercise(intent.exerciseId)
            is ExerciseBrowserIntent.DismissExerciseDetail -> dismissExerciseDetail()
        }
    }

    private fun loadCategories() {
        _state.update { it.copy(isLoadingCategories = true, error = null) }
        viewModelScope.launch {
            try {
                val categories = repository.getCategories()
                _state.update {
                    it.copy(
                        categories = categories,
                        isLoadingCategories = false
                    )
                }
                loadExercises(categoryId = null, offset = 0, append = false)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingCategories = false,
                        error = "Не удалось загрузить категории: ${e.message}"
                    )
                }
            }
        }
    }

    private fun selectCategory(categoryId: Int?) {
        _state.update {
            it.copy(
                selectedCategoryId = categoryId,
                exercises = emptyList(),
                currentOffset = 0,
                hasMore = true,
                isSearchMode = false,
                searchQuery = ""
            )
        }
        loadExercises(categoryId = categoryId, offset = 0, append = false)
    }

    private fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    private fun performSearch() {
        val query = _state.value.searchQuery.trim()
        if (query.isEmpty()) {
            clearSearch()
            return
        }
        _state.update {
            it.copy(
                isLoadingExercises = true,
                isSearchMode = true,
                exercises = emptyList(),
                error = null
            )
        }
        viewModelScope.launch {
            try {
                val results = repository.searchExercises(term = query)
                _state.update {
                    it.copy(
                        exercises = results,
                        isLoadingExercises = false,
                        hasMore = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingExercises = false,
                        error = "Ошибка поиска: ${e.message}"
                    )
                }
            }
        }
    }

    private fun clearSearch() {
        val categoryId = _state.value.selectedCategoryId
        _state.update {
            it.copy(
                searchQuery = "",
                isSearchMode = false,
                exercises = emptyList(),
                currentOffset = 0,
                hasMore = true
            )
        }
        loadExercises(categoryId = categoryId, offset = 0, append = false)
    }

    private fun loadMore() {
        val currentState = _state.value
        if (currentState.isLoadingMore || !currentState.hasMore || currentState.isSearchMode) return
        loadExercises(
            categoryId = currentState.selectedCategoryId,
            offset = currentState.currentOffset,
            append = true
        )
    }

    private fun selectExercise(exerciseId: Int) {
        _state.update { it.copy(selectedExerciseId = exerciseId) }
    }

    private fun dismissExerciseDetail() {
        _state.update { it.copy(selectedExerciseId = null) }
    }

    private fun loadExercises(categoryId: Int?, offset: Int, append: Boolean) {
        val isInitial = !append
        _state.update {
            if (isInitial) it.copy(isLoadingExercises = true, error = null)
            else it.copy(isLoadingMore = true, error = null)
        }

        viewModelScope.launch {
            try {
                val page = repository.getExercises(
                    category = categoryId,
                    limit = 20,
                    offset = offset
                )
                _state.update { current ->
                    val newExercises = if (append) {
                        current.exercises + page.exercises
                    } else {
                        page.exercises
                    }
                    current.copy(
                        exercises = newExercises,
                        isLoadingExercises = false,
                        isLoadingMore = false,
                        currentOffset = offset + page.exercises.size,
                        hasMore = page.hasMore
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingExercises = false,
                        isLoadingMore = false,
                        error = "Не удалось загрузить упражнения: ${e.message}"
                    )
                }
            }
        }
    }
}
