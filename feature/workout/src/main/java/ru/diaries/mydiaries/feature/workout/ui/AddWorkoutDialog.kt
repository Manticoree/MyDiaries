package ru.diaries.mydiaries.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.diaries.mydiaries.feature.workout.data.model.ExerciseType
import ru.diaries.mydiaries.feature.workout.data.model.WorkoutProgram
import ru.diaries.mydiaries.feature.workout.data.model.WorkoutType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutDialog(
    state: AddWorkoutDialogState,
    onIntent: (AddWorkoutDialogIntent) -> Unit,
    onDismiss: () -> Unit
) {
    val coralOrange = Color(0xFFE8845C)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(coralOrange, coralOrange.copy(alpha = 0.7f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Добавить тренировку",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Program dropdown
                ProgramDropdown(
                    programs = state.programs,
                    selectedProgramId = state.selectedProgramId,
                    onProgramSelected = { onIntent(AddWorkoutDialogIntent.ProgramSelected(it)) }
                )

                // Day selector (if program has multiple days)
                val selectedProgram = state.programs.find { it.id == state.selectedProgramId }
                if (selectedProgram != null && selectedProgram.days.size > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedProgram.days.forEachIndexed { index, day ->
                            FilterChip(
                                selected = state.selectedDayIndex == index,
                                onClick = { onIntent(AddWorkoutDialogIntent.DaySelected(index)) },
                                label = { Text(day.name, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = coralOrange.copy(alpha = 0.2f),
                                    selectedLabelColor = coralOrange
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Name field
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { onIntent(AddWorkoutDialogIntent.NameChanged(it)) },
                    label = { Text("Название") },
                    placeholder = { Text("Название тренировки") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = coralOrange,
                        focusedLabelColor = coralOrange
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Workout type chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WorkoutType.entries.forEach { type ->
                        FilterChip(
                            selected = state.workoutType == type,
                            onClick = { onIntent(AddWorkoutDialogIntent.TypeChanged(type)) },
                            label = { Text(workoutTypeDisplayName(type)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = coralOrange.copy(alpha = 0.2f),
                                selectedLabelColor = coralOrange
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Duration field
                OutlinedTextField(
                    value = state.durationMinutes,
                    onValueChange = { onIntent(AddWorkoutDialogIntent.DurationChanged(it)) },
                    label = { Text("Длительность (мин)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = coralOrange,
                        focusedLabelColor = coralOrange
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Exercise list
                if (state.exercises.isNotEmpty()) {
                    Text(
                        text = "Упражнения (${state.exercises.size})",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(
                            items = state.exercises,
                            key = { index, _ -> index }
                        ) { index, exercise ->
                            ExerciseInputRow(
                                exercise = exercise,
                                index = index,
                                onIntent = onIntent,
                                accentColor = coralOrange
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Add exercise button
                TextButton(
                    onClick = { onIntent(AddWorkoutDialogIntent.AddExercise) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = coralOrange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Добавить упражнение", color = coralOrange)
                }

                // Error
                state.error?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Отмена")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = { onIntent(AddWorkoutDialogIntent.Save) },
                        enabled = state.name.isNotBlank() && !state.isSaving,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = coralOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Добавить")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgramDropdown(
    programs: List<WorkoutProgram>,
    selectedProgramId: String?,
    onProgramSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedProgram = programs.find { it.id == selectedProgramId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedProgram?.name ?: "Без программы",
            onValueChange = {},
            readOnly = true,
            label = { Text("Программа") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Без программы") },
                onClick = {
                    onProgramSelected(null)
                    expanded = false
                }
            )
            programs.forEach { program ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(program.name, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                program.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onProgramSelected(program.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ExerciseInputRow(
    exercise: DialogExercise,
    index: Int,
    onIntent: (AddWorkoutDialogIntent) -> Unit,
    accentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = exercise.name,
                    onValueChange = { onIntent(AddWorkoutDialogIntent.ExerciseNameChanged(index, it)) },
                    placeholder = { Text("Упражнение", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall,
                    shape = RoundedCornerShape(8.dp)
                )
                IconButton(
                    onClick = { onIntent(AddWorkoutDialogIntent.RemoveExercise(index)) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (exercise.type == ExerciseType.STRENGTH) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = if (exercise.sets == 0) "" else exercise.sets.toString(),
                        onValueChange = { onIntent(AddWorkoutDialogIntent.ExerciseSetsChanged(index, it)) },
                        label = { Text("Сеты", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.bodySmall,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = if (exercise.reps == 0) "" else exercise.reps.toString(),
                        onValueChange = { onIntent(AddWorkoutDialogIntent.ExerciseRepsChanged(index, it)) },
                        label = { Text("Повт.", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.bodySmall,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = if (exercise.weight == 0.0) "" else exercise.weight.toString(),
                        onValueChange = { onIntent(AddWorkoutDialogIntent.ExerciseWeightChanged(index, it)) },
                        label = { Text("Вес", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = MaterialTheme.typography.bodySmall,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = if (exercise.durationSeconds == 0) "" else (exercise.durationSeconds / 60).toString(),
                        onValueChange = {
                            val minutes = it.toIntOrNull() ?: 0
                            onIntent(AddWorkoutDialogIntent.ExerciseDurationChanged(index, (minutes * 60).toString()))
                        },
                        label = { Text("Мин", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.bodySmall,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = if (exercise.distanceKm == 0.0) "" else exercise.distanceKm.toString(),
                        onValueChange = { onIntent(AddWorkoutDialogIntent.ExerciseDistanceChanged(index, it)) },
                        label = { Text("Км", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = MaterialTheme.typography.bodySmall,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }
}

private fun workoutTypeDisplayName(type: WorkoutType): String = when (type) {
    WorkoutType.STRENGTH -> "Сила"
    WorkoutType.CARDIO -> "Кардио"
    WorkoutType.MIXED -> "Смешан."
}
