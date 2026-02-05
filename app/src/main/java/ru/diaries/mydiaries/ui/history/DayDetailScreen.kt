package ru.diaries.mydiaries.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.diaries.mydiaries.R
import ru.diaries.mydiaries.domain.usecase.history.DaySummary
import ru.diaries.mydiaries.ui.components.DayExpensesCard
import ru.diaries.mydiaries.ui.components.DiaryEntryCard
import ru.diaries.mydiaries.feature.food.ui.DayFoodCard
import ru.diaries.mydiaries.feature.todo.ui.DayTasksCard
import ru.diaries.mydiaries.feature.track.ui.DayTrackCard
import ru.diaries.mydiaries.feature.track.ui.FullScreenMapDialog
import ru.diaries.mydiaries.feature.video.ui.DayVideosCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(
    daySummary: DaySummary,
    onBack: () -> Unit
) {
    val dateText = formatDayDetailDate(daySummary.date)
    var showMapDialog by remember { mutableStateOf(false) }

    if (showMapDialog && daySummary.track != null) {
        FullScreenMapDialog(
            track = daySummary.track!!,
            isTracking = false,
            onToggleTracking = {},
            onDismiss = { showMapDialog = false },
            titleText = stringResource(R.string.full_map),
            distanceLabel = stringResource(R.string.track_distance),
            durationLabel = stringResource(R.string.track_duration),
            speedLabel = stringResource(R.string.track_avg_speed),
            stepsLabel = stringResource(R.string.track_steps),
            kmUnit = stringResource(R.string.track_km),
            kmhUnit = stringResource(R.string.track_kmh),
            showTrackingButton = false
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Track card
            daySummary.track?.let { track ->
                DayTrackCard(
                    track = track,
                    onMapClick = { showMapDialog = true },
                    onDelete = {},
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    isExpanded = true,
                    onToggleExpand = {},
                    cardTitle = stringResource(R.string.track_for_day),
                    distanceLabel = stringResource(R.string.track_distance),
                    durationLabel = stringResource(R.string.track_duration),
                    speedLabel = stringResource(R.string.track_avg_speed),
                    stepsLabel = stringResource(R.string.track_steps),
                    kmUnit = stringResource(R.string.track_km),
                    kmhUnit = stringResource(R.string.track_kmh),
                    trackingActiveText = stringResource(R.string.tracking_active),
                    mapButtonText = stringResource(R.string.open_map)
                )
            }

            // Tasks card
            if (daySummary.tasks.isNotEmpty()) {
                DayTasksCard(
                    tasks = daySummary.tasks,
                    onToggleTask = { _, _ -> },
                    onDeleteTask = {},
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    isExpanded = true,
                    onToggleExpand = {},
                    cardTitle = stringResource(R.string.tasks_for_day),
                    completedText = stringResource(R.string.task_completed_short)
                )
            }

            // Food card
            if (daySummary.foodEntries.isNotEmpty()) {
                DayFoodCard(
                    foodEntries = daySummary.foodEntries,
                    onDeleteFood = {},
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    isExpanded = true,
                    onToggleExpand = {},
                    cardTitle = stringResource(R.string.food_for_day)
                )
            }

            // Videos card
            if (daySummary.videos.isNotEmpty()) {
                DayVideosCard(
                    videos = daySummary.videos,
                    onVideoClick = {},
                    onDeleteVideo = {},
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    isExpanded = true,
                    onToggleExpand = {},
                    cardTitle = stringResource(R.string.videos_for_day)
                )
            }

            // Diary entries
            daySummary.entries.forEach { entry ->
                DiaryEntryCard(
                    entry = entry,
                    onClick = {},
                    onDelete = {},
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Expenses card
            if (daySummary.expenses.isNotEmpty()) {
                DayExpensesCard(
                    expenses = daySummary.expenses,
                    onDeleteExpense = {},
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    isExpanded = true,
                    onToggleExpand = {}
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun formatDayDetailDate(date: LocalDate): String {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    return when (date) {
        today -> stringResource(R.string.date_today)
        yesterday -> stringResource(R.string.date_yesterday)
        else -> date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru")))
    }
}
