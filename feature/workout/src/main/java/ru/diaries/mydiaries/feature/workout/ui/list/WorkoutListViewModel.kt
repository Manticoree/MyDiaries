package ru.diaries.mydiaries.feature.workout.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.feature.workout.data.model.Workout
import ru.diaries.mydiaries.feature.workout.data.repository.WorkoutRepository
import ru.diaries.mydiaries.feature.workout.domain.usecase.GetWorkoutProgramsUseCase
import javax.inject.Inject

@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val getWorkoutProgramsUseCase: GetWorkoutProgramsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutListState())
    val state: StateFlow<WorkoutListState> = _state.asStateFlow()

    init {
        _state.update { it.copy(programs = getWorkoutProgramsUseCase()) }
        handleIntent(WorkoutListIntent.LoadData)
    }

    fun handleIntent(intent: WorkoutListIntent) {
        when (intent) {
            is WorkoutListIntent.LoadData -> loadData()
            is WorkoutListIntent.FilterByType -> filterByType(intent.filter)
            is WorkoutListIntent.DeleteWorkout -> deleteWorkout(intent.id)
            is WorkoutListIntent.StartWorkout -> startWorkout(intent.workout)
            is WorkoutListIntent.CloseWorkoutDetail -> closeWorkoutDetail()
            is WorkoutListIntent.ShowAddWorkoutDialog -> showAddWorkoutDialog(null)
            is WorkoutListIntent.ShowAddWorkoutWithProgram -> showAddWorkoutDialog(intent.programId)
            is WorkoutListIntent.HideAddWorkoutDialog -> hideAddWorkoutDialog()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            workoutRepository.getAllWorkouts()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { workouts ->
                    _state.update {
                        it.copy(
                            workouts = workouts,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun filterByType(filter: WorkoutTypeFilter) {
        _state.update { it.copy(selectedFilter = filter) }
    }

    private fun deleteWorkout(id: String) {
        viewModelScope.launch {
            workoutRepository.deleteWorkout(id)
        }
    }

    private fun startWorkout(workout: Workout) {
        _state.update { it.copy(activeWorkout = workout) }
    }

    private fun closeWorkoutDetail() {
        _state.update { it.copy(activeWorkout = null) }
    }

    private fun showAddWorkoutDialog(programId: String?) {
        _state.update {
            it.copy(
                showAddWorkoutDialog = true,
                addWorkoutDialogKey = "workout_list_${System.currentTimeMillis()}",
                selectedProgramId = programId
            )
        }
    }

    private fun hideAddWorkoutDialog() {
        _state.update {
            it.copy(
                showAddWorkoutDialog = false,
                addWorkoutDialogKey = "",
                selectedProgramId = null
            )
        }
    }
}
