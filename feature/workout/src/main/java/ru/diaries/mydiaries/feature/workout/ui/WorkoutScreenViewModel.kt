package ru.diaries.mydiaries.feature.workout.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.feature.workout.data.model.ExerciseSet
import ru.diaries.mydiaries.feature.workout.data.model.Workout
import ru.diaries.mydiaries.feature.workout.domain.usecase.SaveWorkoutUseCase
import javax.inject.Inject

@HiltViewModel
class WorkoutScreenViewModel @Inject constructor(
    private val saveWorkoutUseCase: SaveWorkoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutScreenState())
    val state: StateFlow<WorkoutScreenState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<WorkoutScreenEffect>()
    val effect: SharedFlow<WorkoutScreenEffect> = _effect.asSharedFlow()

    private var restTimerJob: Job? = null
    private var workoutTimerJob: Job? = null

    fun handleIntent(intent: WorkoutScreenIntent) {
        when (intent) {
            is WorkoutScreenIntent.LoadWorkout -> loadWorkout(intent.workout)
            is WorkoutScreenIntent.ToggleSetCompletion -> toggleSetCompletion(intent.exerciseId, intent.setId)
            is WorkoutScreenIntent.UpdateSetReps -> updateSetReps(intent.setId, intent.reps)
            is WorkoutScreenIntent.UpdateSetWeight -> updateSetWeight(intent.setId, intent.weight)
            is WorkoutScreenIntent.StartRestTimer -> startRestTimer()
            is WorkoutScreenIntent.StopRestTimer -> stopRestTimer()
            is WorkoutScreenIntent.ToggleWorkoutTimer -> toggleWorkoutTimer()
            is WorkoutScreenIntent.FinishWorkout -> finishWorkout()
        }
    }

    private fun loadWorkout(workout: Workout) {
        _state.update { it.copy(workout = workout, isLoading = false) }
    }

    private fun toggleSetCompletion(exerciseId: String, setId: String) {
        _state.update { state ->
            val updatedWorkout = state.workout?.let { workout ->
                workout.copy(
                    exercises = workout.exercises.map { exercise ->
                        if (exercise.id == exerciseId) {
                            exercise.copy(
                                sets = exercise.sets.map { set ->
                                    if (set.id == setId) {
                                        set.copy(isCompleted = !set.isCompleted)
                                    } else set
                                }
                            )
                        } else exercise
                    }
                )
            }
            state.copy(workout = updatedWorkout)
        }
    }

    private fun updateSetReps(setId: String, reps: Int) {
        _state.update { state ->
            val updatedWorkout = state.workout?.let { workout ->
                workout.copy(
                    exercises = workout.exercises.map { exercise ->
                        exercise.copy(
                            sets = exercise.sets.map { set ->
                                if (set.id == setId) set.copy(reps = reps) else set
                            }
                        )
                    }
                )
            }
            state.copy(workout = updatedWorkout)
        }
    }

    private fun updateSetWeight(setId: String, weight: Double) {
        _state.update { state ->
            val updatedWorkout = state.workout?.let { workout ->
                workout.copy(
                    exercises = workout.exercises.map { exercise ->
                        exercise.copy(
                            sets = exercise.sets.map { set ->
                                if (set.id == setId) set.copy(weight = weight) else set
                            }
                        )
                    }
                )
            }
            state.copy(workout = updatedWorkout)
        }
    }

    private fun startRestTimer() {
        restTimerJob?.cancel()
        _state.update { it.copy(restTimerSeconds = 90, isRestTimerRunning = true) }
        restTimerJob = viewModelScope.launch {
            while (_state.value.restTimerSeconds > 0) {
                delay(1000)
                _state.update { it.copy(restTimerSeconds = it.restTimerSeconds - 1) }
            }
            _state.update { it.copy(isRestTimerRunning = false) }
        }
    }

    private fun stopRestTimer() {
        restTimerJob?.cancel()
        _state.update { it.copy(restTimerSeconds = 0, isRestTimerRunning = false) }
    }

    private fun toggleWorkoutTimer() {
        if (_state.value.isTimerRunning) {
            workoutTimerJob?.cancel()
            _state.update { it.copy(isTimerRunning = false) }
        } else {
            _state.update { it.copy(isTimerRunning = true) }
            workoutTimerJob = viewModelScope.launch {
                while (true) {
                    delay(1000)
                    _state.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
        }
    }

    private fun finishWorkout() {
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()

        val workout = _state.value.workout ?: return
        val durationMinutes = (_state.value.elapsedSeconds / 60).toInt()
        val updatedWorkout = workout.copy(
            durationMinutes = if (durationMinutes > 0) durationMinutes else workout.durationMinutes
        )

        viewModelScope.launch {
            saveWorkoutUseCase(updatedWorkout)
            _effect.emit(WorkoutScreenEffect.WorkoutFinished)
        }
    }

    override fun onCleared() {
        super.onCleared()
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
    }
}
