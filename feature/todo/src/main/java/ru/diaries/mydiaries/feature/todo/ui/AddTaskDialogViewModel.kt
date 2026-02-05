package ru.diaries.mydiaries.feature.todo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.feature.todo.domain.usecase.SaveTasksUseCase
import javax.inject.Inject

@HiltViewModel
class AddTaskDialogViewModel @Inject constructor(
    private val saveTasksUseCase: SaveTasksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddTaskDialogState())
    val state: StateFlow<AddTaskDialogState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AddTaskDialogEffect>()
    val effect: SharedFlow<AddTaskDialogEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: AddTaskDialogIntent) {
        when (intent) {
            is AddTaskDialogIntent.TitleChanged -> updateTitle(intent.index, intent.title)
            is AddTaskDialogIntent.AddField -> addField()
            is AddTaskDialogIntent.RemoveField -> removeField(intent.index)
            is AddTaskDialogIntent.Save -> saveTasks()
            is AddTaskDialogIntent.ClearError -> clearError()
        }
    }

    private fun updateTitle(index: Int, title: String) {
        _state.update { currentState ->
            val updatedTitles = currentState.taskTitles.toMutableList().apply {
                if (index in indices) {
                    set(index, title)
                }
            }
            currentState.copy(taskTitles = updatedTitles, error = null)
        }
    }

    private fun addField() {
        _state.update { currentState ->
            currentState.copy(taskTitles = currentState.taskTitles + "")
        }
    }

    private fun removeField(index: Int) {
        _state.update { currentState ->
            if (currentState.taskTitles.size > 1 && index in currentState.taskTitles.indices) {
                val updatedTitles = currentState.taskTitles.toMutableList().apply {
                    removeAt(index)
                }
                currentState.copy(taskTitles = updatedTitles)
            } else {
                currentState
            }
        }
    }

    private fun saveTasks() {
        val titles = _state.value.taskTitles

        if (titles.all { it.isBlank() }) {
            _state.update { it.copy(error = "Please enter at least one task") }
            return
        }

        _state.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            saveTasksUseCase(titles).fold(
                onSuccess = { count ->
                    _state.update { it.copy(isSaving = false) }
                    _effect.emit(AddTaskDialogEffect.SaveSuccess(count))
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            error = e.message ?: "Failed to save tasks"
                        )
                    }
                }
            )
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
