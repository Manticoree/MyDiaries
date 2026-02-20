package ru.diaries.mydiaries.feature.workout.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import ru.diaries.mydiaries.feature.workout.data.model.Exercise
import ru.diaries.mydiaries.feature.workout.data.model.Workout
import ru.diaries.mydiaries.feature.workout.data.model.WorkoutProgram
import ru.diaries.mydiaries.feature.workout.data.model.WorkoutType
import ru.diaries.mydiaries.feature.workout.ui.AddWorkoutDialog
import ru.diaries.mydiaries.feature.workout.ui.AddWorkoutDialogEffect
import ru.diaries.mydiaries.feature.workout.ui.AddWorkoutDialogIntent
import ru.diaries.mydiaries.feature.workout.ui.AddWorkoutDialogViewModel
import ru.diaries.mydiaries.feature.workout.ui.WorkoutScreenDialog
import ru.diaries.mydiaries.feature.workout.ui.WorkoutScreenViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val CoralOrange = Color(0xFFE8845C)
private val DeepPurple = Color(0xFF7B68AE)
private val CardioGreen = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutListScreen(
    viewModel: WorkoutListViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsState()

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
                    title = { Text("Тренировки") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.handleIntent(WorkoutListIntent.ShowAddWorkoutDialog) },
                    containerColor = CoralOrange,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        ) { paddingValues ->
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CoralOrange)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // Stats banner
                    item(key = "stats") {
                        StatsBanner(
                            totalWorkouts = state.totalWorkouts,
                            averageDuration = state.averageDuration,
                            thisWeekCount = state.thisWeekCount,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // Filter chips
                    item(key = "filters") {
                        FilterChipsRow(
                            selectedFilter = state.selectedFilter,
                            onFilterSelected = { viewModel.handleIntent(WorkoutListIntent.FilterByType(it)) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    // Programs section
                    item(key = "programs_header") {
                        Text(
                            text = "Программы тренировок",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
                        )
                    }

                    item(key = "programs") {
                        ProgramsRow(
                            programs = state.programs,
                            onProgramClick = { programId ->
                                viewModel.handleIntent(WorkoutListIntent.ShowAddWorkoutWithProgram(programId))
                            }
                        )
                    }

                    // Workouts list header
                    item(key = "workouts_header") {
                        Text(
                            text = "История тренировок",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }

                    // Grouped workouts by date
                    val grouped = state.filteredWorkouts
                        .sortedByDescending { it.createdAt }
                        .groupBy { it.date }
                        .toSortedMap(compareByDescending { it })

                    if (grouped.isEmpty()) {
                        item(key = "empty") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Нет тренировок",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    grouped.forEach { (date, workouts) ->
                        item(key = "date_$date") {
                            DateHeader(
                                date = date,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }

                        items(
                            items = workouts,
                            key = { it.id }
                        ) { workout ->
                            WorkoutListItem(
                                workout = workout,
                                onClick = { viewModel.handleIntent(WorkoutListIntent.StartWorkout(workout)) },
                                onDelete = { viewModel.handleIntent(WorkoutListIntent.DeleteWorkout(workout.id)) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Add Workout Dialog
        if (state.showAddWorkoutDialog) {
            AddWorkoutDialogFromList(
                key = state.addWorkoutDialogKey,
                selectedProgramId = state.selectedProgramId,
                onDismiss = { viewModel.handleIntent(WorkoutListIntent.HideAddWorkoutDialog) },
                onSaveSuccess = { viewModel.handleIntent(WorkoutListIntent.HideAddWorkoutDialog) }
            )
        }

        // Active Workout Screen
        state.activeWorkout?.let { workout ->
            val workoutScreenViewModel: WorkoutScreenViewModel = hiltViewModel(
                key = "workout_list_screen_${workout.id}"
            )
            WorkoutScreenDialog(
                workout = workout,
                viewModel = workoutScreenViewModel,
                onDismiss = { viewModel.handleIntent(WorkoutListIntent.CloseWorkoutDetail) }
            )
        }
    }
}

@Composable
private fun StatsBanner(
    totalWorkouts: Int,
    averageDuration: Int,
    thisWeekCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CoralOrange.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = totalWorkouts.toString(), label = "Всего")
            StatItem(value = "${averageDuration} мин", label = "Ср. время")
            StatItem(value = thisWeekCount.toString(), label = "На неделе")
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = CoralOrange
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FilterChipsRow(
    selectedFilter: WorkoutTypeFilter,
    onFilterSelected: (WorkoutTypeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WorkoutTypeFilter.entries.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filterLabel(filter), style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CoralOrange.copy(alpha = 0.2f),
                    selectedLabelColor = CoralOrange
                )
            )
        }
    }
}

private fun filterLabel(filter: WorkoutTypeFilter): String = when (filter) {
    WorkoutTypeFilter.ALL -> "Все"
    WorkoutTypeFilter.STRENGTH -> "Сила"
    WorkoutTypeFilter.CARDIO -> "Кардио"
    WorkoutTypeFilter.MIXED -> "Смешан."
}

@Composable
private fun ProgramsRow(
    programs: List<WorkoutProgram>,
    onProgramClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(programs, key = { it.id }) { program ->
            ProgramCard(
                program = program,
                onClick = { onProgramClick(program.id) }
            )
        }
    }
}

@Composable
private fun ProgramCard(
    program: WorkoutProgram,
    onClick: () -> Unit
) {
    val gradientColors = when (program.type) {
        WorkoutType.STRENGTH -> listOf(CoralOrange, DeepPurple)
        WorkoutType.CARDIO -> listOf(CardioGreen, Color(0xFF2E7D32))
        WorkoutType.MIXED -> listOf(DeepPurple, CoralOrange)
    }

    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Brush.linearGradient(gradientColors))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = program.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${program.days.size} дн.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun DateHeader(
    date: LocalDate,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val text = when (date) {
        today -> "Сегодня"
        yesterday -> "Вчера"
        else -> date.format(
            DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))
        )
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
private fun WorkoutListItem(
    workout: Workout,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                workoutTypeColor(workout.type),
                                workoutTypeColor(workout.type).copy(alpha = 0.6f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = workoutTypeColor(workout.type).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = workoutTypeLabel(workout.type),
                            style = MaterialTheme.typography.labelSmall,
                            color = workoutTypeColor(workout.type),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (workout.exercises.isNotEmpty()) {
                        Text(
                            text = "${workout.exercises.size} упр.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (workout.durationMinutes > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${workout.durationMinutes} мин",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Start button
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PlayArrow,
                    contentDescription = null,
                    tint = CoralOrange,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AddWorkoutDialogFromList(
    key: String,
    selectedProgramId: String?,
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val addViewModel: AddWorkoutDialogViewModel = hiltViewModel(key = key)
    val addState by addViewModel.state.collectAsState()

    LaunchedEffect(selectedProgramId) {
        if (selectedProgramId != null) {
            addViewModel.handleIntent(AddWorkoutDialogIntent.ProgramSelected(selectedProgramId))
        }
    }

    LaunchedEffect(Unit) {
        addViewModel.effect.collect { effect ->
            when (effect) {
                AddWorkoutDialogEffect.SaveSuccess -> onSaveSuccess()
            }
        }
    }

    AddWorkoutDialog(
        state = addState,
        onIntent = { addViewModel.handleIntent(it) },
        onDismiss = onDismiss
    )
}

private fun workoutTypeLabel(type: WorkoutType): String = when (type) {
    WorkoutType.STRENGTH -> "Сила"
    WorkoutType.CARDIO -> "Кардио"
    WorkoutType.MIXED -> "Смешан."
}

private fun workoutTypeColor(type: WorkoutType): Color = when (type) {
    WorkoutType.STRENGTH -> CoralOrange
    WorkoutType.CARDIO -> CardioGreen
    WorkoutType.MIXED -> DeepPurple
}


