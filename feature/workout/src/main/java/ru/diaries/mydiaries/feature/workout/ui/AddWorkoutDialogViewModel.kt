package ru.diaries.mydiaries.feature.workout.ui

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
import ru.diaries.mydiaries.feature.workout.data.model.Exercise
import ru.diaries.mydiaries.feature.workout.data.model.ExerciseSet
import ru.diaries.mydiaries.feature.workout.data.model.ExerciseType
import ru.diaries.mydiaries.feature.workout.data.model.Workout
import ru.diaries.mydiaries.feature.workout.data.model.WorkoutType
import ru.diaries.mydiaries.feature.workout.domain.usecase.GetWorkoutProgramsUseCase
import ru.diaries.mydiaries.feature.workout.domain.usecase.SaveWorkoutUseCase
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddWorkoutDialogViewModel @Inject constructor(
    private val saveWorkoutUseCase: SaveWorkoutUseCase,
    private val getWorkoutProgramsUseCase: GetWorkoutProgramsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddWorkoutDialogState())
    val state: StateFlow<AddWorkoutDialogState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AddWorkoutDialogEffect>()
    val effect: SharedFlow<AddWorkoutDialogEffect> = _effect.asSharedFlow()

    init {
        _state.update { it.copy(programs = getWorkoutProgramsUseCase()) }
    }

    fun handleIntent(intent: AddWorkoutDialogIntent) {
        when (intent) {
            is AddWorkoutDialogIntent.NameChanged -> _state.update { it.copy(name = intent.name) }
            is AddWorkoutDialogIntent.TypeChanged -> _state.update { it.copy(workoutType = intent.type) }
            is AddWorkoutDialogIntent.DurationChanged -> _state.update { it.copy(durationMinutes = intent.duration) }
            is AddWorkoutDialogIntent.NotesChanged -> _state.update { it.copy(notes = intent.notes) }
            is AddWorkoutDialogIntent.ProgramSelected -> selectProgram(intent.programId)
            is AddWorkoutDialogIntent.DaySelected -> selectDay(intent.dayIndex)
            is AddWorkoutDialogIntent.AddExercise -> addExercise()
            is AddWorkoutDialogIntent.RemoveExercise -> removeExercise(intent.index)
            is AddWorkoutDialogIntent.ExerciseNameChanged -> updateExercise(intent.index) { it.copy(name = intent.name) }
            is AddWorkoutDialogIntent.ExerciseSetsChanged -> updateExercise(intent.index) { it.copy(sets = intent.sets.toIntOrNull() ?: 0) }
            is AddWorkoutDialogIntent.ExerciseRepsChanged -> updateExercise(intent.index) { it.copy(reps = intent.reps.toIntOrNull() ?: 0) }
            is AddWorkoutDialogIntent.ExerciseWeightChanged -> updateExercise(intent.index) { it.copy(weight = intent.weight.toDoubleOrNull() ?: 0.0) }
            is AddWorkoutDialogIntent.ExerciseDurationChanged -> updateExercise(intent.index) { it.copy(durationSeconds = intent.duration.toIntOrNull() ?: 0) }
            is AddWorkoutDialogIntent.ExerciseDistanceChanged -> updateExercise(intent.index) { it.copy(distanceKm = intent.distance.toDoubleOrNull() ?: 0.0) }
            is AddWorkoutDialogIntent.ExerciseTypeChanged -> updateExercise(intent.index) { it.copy(type = intent.type) }
            is AddWorkoutDialogIntent.Save -> save()
            is AddWorkoutDialogIntent.ClearError -> _state.update { it.copy(error = null) }
        }
    }

    private fun selectProgram(programId: String?) {
        val program = _state.value.programs.find { it.id == programId }
        if (program == null) {
            _state.update { it.copy(selectedProgramId = null, selectedDayIndex = 0) }
            return
        }

        val dayIndex = 0
        val exercises = program.days.getOrNull(dayIndex)?.templateExercises?.map { template ->
            DialogExercise(
                name = template.name,
                type = template.type,
                sets = template.defaultSets,
                reps = template.defaultReps,
                durationSeconds = template.defaultDurationSeconds,
                distanceKm = template.defaultDistanceKm
            )
        } ?: emptyList()

        _state.update {
            it.copy(
                selectedProgramId = programId,
                selectedDayIndex = dayIndex,
                name = program.name + " - " + (program.days.getOrNull(dayIndex)?.name ?: ""),
                workoutType = program.type,
                exercises = exercises
            )
        }
    }

    private fun selectDay(dayIndex: Int) {
        val program = _state.value.programs.find { it.id == _state.value.selectedProgramId } ?: return
        val day = program.days.getOrNull(dayIndex) ?: return

        val exercises = day.templateExercises.map { template ->
            DialogExercise(
                name = template.name,
                type = template.type,
                sets = template.defaultSets,
                reps = template.defaultReps,
                durationSeconds = template.defaultDurationSeconds,
                distanceKm = template.defaultDistanceKm
            )
        }

        _state.update {
            it.copy(
                selectedDayIndex = dayIndex,
                name = program.name + " - " + day.name,
                exercises = exercises
            )
        }
    }

    private fun addExercise() {
        _state.update {
            val type = if (it.workoutType == WorkoutType.CARDIO) ExerciseType.CARDIO else ExerciseType.STRENGTH
            it.copy(exercises = it.exercises + DialogExercise(type = type))
        }
    }

    private fun removeExercise(index: Int) {
        _state.update {
            it.copy(exercises = it.exercises.filterIndexed { i, _ -> i != index })
        }
    }

    private fun updateExercise(index: Int, transform: (DialogExercise) -> DialogExercise) {
        _state.update { state ->
            state.copy(
                exercises = state.exercises.mapIndexed { i, exercise ->
                    if (i == index) transform(exercise) else exercise
                }
            )
        }
    }

    private fun save() {
        val currentState = _state.value
        if (currentState.name.isBlank()) {
            _state.update { it.copy(error = "Введите название тренировки") }
            return
        }

        _state.update { it.copy(isSaving = true, error = null) }

        val workoutId = UUID.randomUUID().toString()
        val exercises = currentState.exercises
            .filter { it.name.isNotBlank() }
            .mapIndexed { index, dialogExercise ->
                val exerciseId = UUID.randomUUID().toString()
                val sets = if (dialogExercise.type == ExerciseType.CARDIO) {
                    listOf(
                        ExerciseSet(
                            id = UUID.randomUUID().toString(),
                            exerciseId = exerciseId,
                            setNumber = 1,
                            durationSeconds = dialogExercise.durationSeconds,
                            distanceKm = dialogExercise.distanceKm,
                            isCompleted = false
                        )
                    )
                } else {
                    (1..dialogExercise.sets).map { setNum ->
                        ExerciseSet(
                            id = UUID.randomUUID().toString(),
                            exerciseId = exerciseId,
                            setNumber = setNum,
                            reps = dialogExercise.reps,
                            weight = dialogExercise.weight,
                            isCompleted = false
                        )
                    }
                }
                Exercise(
                    id = exerciseId,
                    workoutId = workoutId,
                    name = dialogExercise.name,
                    type = dialogExercise.type,
                    order = index,
                    sets = sets
                )
            }

        val workout = Workout(
            id = workoutId,
            name = currentState.name.trim(),
            type = currentState.workoutType,
            durationMinutes = currentState.durationMinutes.toIntOrNull() ?: 0,
            programId = currentState.selectedProgramId,
            notes = currentState.notes.trim(),
            exercises = exercises
        )

        viewModelScope.launch {
            saveWorkoutUseCase(workout).fold(
                onSuccess = {
                    _state.update { it.copy(isSaving = false) }
                    _effect.emit(AddWorkoutDialogEffect.SaveSuccess)
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(isSaving = false, error = e.message ?: "Ошибка сохранения")
                    }
                }
            )
        }
    }
}
