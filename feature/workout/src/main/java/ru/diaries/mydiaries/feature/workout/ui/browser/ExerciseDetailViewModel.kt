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
class ExerciseDetailViewModel @Inject constructor(
    private val repository: ExerciseBrowserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExerciseDetailState())
    val state: StateFlow<ExerciseDetailState> = _state.asStateFlow()

    fun loadExerciseDetail(exerciseId: Int) {
        _state.update { ExerciseDetailState(isLoading = true) }
        viewModelScope.launch {
            try {
                val exercise = repository.getExerciseById(exerciseId)
                _state.update {
                    if (exercise != null) {
                        ExerciseDetailState(exercise = exercise)
                    } else {
                        ExerciseDetailState(error = "Упражнение не найдено")
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    ExerciseDetailState(error = "Ошибка загрузки: ${e.message}")
                }
            }
        }
    }
}
