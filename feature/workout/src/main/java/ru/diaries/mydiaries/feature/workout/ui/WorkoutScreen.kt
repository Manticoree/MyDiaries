package ru.diaries.mydiaries.feature.workout.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.diaries.mydiaries.feature.workout.data.model.Exercise
import ru.diaries.mydiaries.feature.workout.data.model.ExerciseSet
import ru.diaries.mydiaries.feature.workout.data.model.ExerciseType
import ru.diaries.mydiaries.feature.workout.data.model.Workout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreenDialog(
    workout: Workout,
    viewModel: WorkoutScreenViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val coralOrange = Color(0xFFE8845C)

    LaunchedEffect(workout) {
        viewModel.handleIntent(WorkoutScreenIntent.LoadWorkout(workout))
        viewModel.handleIntent(WorkoutScreenIntent.ToggleWorkoutTimer)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                WorkoutScreenEffect.WorkoutFinished -> onDismiss()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = workout.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = formatElapsedTime(state.elapsedSeconds),
                                style = MaterialTheme.typography.labelSmall,
                                color = coralOrange
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.handleIntent(WorkoutScreenIntent.ToggleWorkoutTimer) }
                        ) {
                            Icon(
                                imageVector = if (state.isTimerRunning) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                                contentDescription = null,
                                tint = coralOrange
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Rest timer
                if (state.isRestTimerRunning) {
                    RestTimerBar(
                        seconds = state.restTimerSeconds,
                        onStop = { viewModel.handleIntent(WorkoutScreenIntent.StopRestTimer) },
                        accentColor = coralOrange
                    )
                }

                // Exercise list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    state.workout?.exercises?.let { exercises ->
                        items(exercises, key = { it.id }) { exercise ->
                            ExerciseCard(
                                exercise = exercise,
                                onToggleSet = { setId ->
                                    viewModel.handleIntent(
                                        WorkoutScreenIntent.ToggleSetCompletion(exercise.id, setId)
                                    )
                                    if (!state.isRestTimerRunning) {
                                        viewModel.handleIntent(WorkoutScreenIntent.StartRestTimer)
                                    }
                                },
                                onUpdateReps = { setId, reps ->
                                    viewModel.handleIntent(WorkoutScreenIntent.UpdateSetReps(setId, reps))
                                },
                                onUpdateWeight = { setId, weight ->
                                    viewModel.handleIntent(WorkoutScreenIntent.UpdateSetWeight(setId, weight))
                                },
                                accentColor = coralOrange
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // Finish button
                Button(
                    onClick = { viewModel.handleIntent(WorkoutScreenIntent.FinishWorkout) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = coralOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Завершить тренировку",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RestTimerBar(
    seconds: Int,
    onStop: () -> Unit,
    accentColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = accentColor.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Timer,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Отдых: ${seconds / 60}:%02d".format(seconds % 60),
                    style = MaterialTheme.typography.titleSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
            OutlinedButton(
                onClick = onStop,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Пропустить", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: Exercise,
    onToggleSet: (String) -> Unit,
    onUpdateReps: (String, Int) -> Unit,
    onUpdateWeight: (String, Double) -> Unit,
    accentColor: Color
) {
    val completedSets = exercise.sets.count { it.isCompleted }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (completedSets == exercise.sets.size && exercise.sets.isNotEmpty()) {
                        accentColor.copy(alpha = 0.15f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = "$completedSets/${exercise.sets.size}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = if (completedSets == exercise.sets.size && exercise.sets.isNotEmpty()) {
                            accentColor
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (exercise.type == ExerciseType.STRENGTH) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Сет", style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
                    Text("Повт.", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text("Вес (кг)", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Box(modifier = Modifier.width(40.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            exercise.sets.forEach { set ->
                SetRow(
                    set = set,
                    exerciseType = exercise.type,
                    onToggle = { onToggleSet(set.id) },
                    onUpdateReps = { onUpdateReps(set.id, it) },
                    onUpdateWeight = { onUpdateWeight(set.id, it) },
                    accentColor = accentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun SetRow(
    set: ExerciseSet,
    exerciseType: ExerciseType,
    onToggle: () -> Unit,
    onUpdateReps: (Int) -> Unit,
    onUpdateWeight: (Double) -> Unit,
    accentColor: Color
) {
    val bgColor by animateColorAsState(
        targetValue = if (set.isCompleted) {
            accentColor.copy(alpha = 0.08f)
        } else {
            Color.Transparent
        },
        label = "setBg"
    )

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        if (exerciseType == ExerciseType.STRENGTH) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${set.setNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.Center
                )
                OutlinedTextField(
                    value = if (set.reps == 0) "" else set.reps.toString(),
                    onValueChange = { onUpdateReps(it.toIntOrNull() ?: 0) },
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(6.dp)
                )
                OutlinedTextField(
                    value = if (set.weight == 0.0) "" else set.weight.toString(),
                    onValueChange = { onUpdateWeight(it.toDoubleOrNull() ?: 0.0) },
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(6.dp)
                )
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                if (set.isCompleted) {
                                    Brush.linearGradient(listOf(accentColor, accentColor.copy(alpha = 0.7f)))
                                } else {
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (set.isCompleted) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (set.durationSeconds > 0) {
                        Text(
                            text = "${set.durationSeconds / 60} мин",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (set.distanceKm > 0) {
                        Text(
                            text = "%.1f км".format(set.distanceKm),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                if (set.isCompleted) {
                                    Brush.linearGradient(listOf(accentColor, accentColor.copy(alpha = 0.7f)))
                                } else {
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (set.isCompleted) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatElapsedTime(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(mins, secs)
}
