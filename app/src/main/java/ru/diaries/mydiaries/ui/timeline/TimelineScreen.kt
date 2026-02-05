package ru.diaries.mydiaries.ui.timeline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.WindowInsets
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import ru.diaries.mydiaries.R
import ru.diaries.mydiaries.ui.components.ActionChoiceDialog
import ru.diaries.mydiaries.ui.components.DateGroupHeader
import ru.diaries.mydiaries.ui.components.DayExpensesCard
import ru.diaries.mydiaries.ui.components.DiaryEntryCard
import ru.diaries.mydiaries.ui.components.EmptyStateView
import ru.diaries.mydiaries.ui.components.MiniPieChart
import ru.diaries.mydiaries.ui.editor.EditorScreen
import ru.diaries.mydiaries.ui.expense.AddExpenseDialog
import ru.diaries.mydiaries.ui.expense.AddExpenseDialogEffect
import ru.diaries.mydiaries.ui.expense.AddExpenseDialogIntent
import ru.diaries.mydiaries.ui.expense.AddExpenseDialogViewModel
import ru.diaries.mydiaries.ui.expense.ExpenseStatsDialog
import ru.diaries.mydiaries.feature.todo.ui.AddTaskDialog
import ru.diaries.mydiaries.feature.todo.ui.AddTaskDialogEffect
import ru.diaries.mydiaries.feature.todo.ui.AddTaskDialogIntent
import ru.diaries.mydiaries.feature.todo.ui.AddTaskDialogViewModel
import ru.diaries.mydiaries.feature.todo.ui.DayTasksCard
import ru.diaries.mydiaries.feature.food.data.model.FoodEntry
import ru.diaries.mydiaries.feature.food.ui.AddFoodDialog
import ru.diaries.mydiaries.feature.food.ui.DayFoodCard
import ru.diaries.mydiaries.feature.track.data.model.DailyTrack
import ru.diaries.mydiaries.feature.track.ui.DayTrackCard
import ru.diaries.mydiaries.feature.track.ui.FullScreenMapDialog
import ru.diaries.mydiaries.feature.video.data.model.Video
import ru.diaries.mydiaries.feature.video.ui.AddVideoDialog
import ru.diaries.mydiaries.feature.video.ui.DayVideosCard
import ru.diaries.mydiaries.feature.video.ui.VideoPlayerDialog
import ru.diaries.mydiaries.feature.food.ui.AddFoodDialogEffect
import ru.diaries.mydiaries.feature.food.ui.AddFoodDialogIntent
import ru.diaries.mydiaries.feature.food.ui.AddFoodDialogViewModel
import ru.diaries.mydiaries.feature.video.ui.AddVideoDialogEffect
import ru.diaries.mydiaries.feature.video.ui.AddVideoDialogIntent
import ru.diaries.mydiaries.feature.video.ui.AddVideoDialogViewModel
import ru.diaries.mydiaries.ui.theme.GoldenHoney
import ru.diaries.mydiaries.ui.theme.NumberStyle
import ru.diaries.mydiaries.ui.theme.SageGreen
import ru.diaries.mydiaries.ui.theme.Terracotta
import ru.diaries.mydiaries.ui.theme.WarmBrown
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel = hiltViewModel(),
    showFab: Boolean = true,
    onStepCardClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val greeting = getGreetingText()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = formatTodayDate(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = { viewModel.handleIntent(TimelineIntent.ShowActionChoiceDialog) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            spotColor = WarmBrown.copy(alpha = 0.3f)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_entry)
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item(key = "summary_row") {
                            TodaySummaryRow(
                                expenses = state.todayExpenses,
                                steps = state.todaySteps,
                                isStepCounterRunning = state.isStepCounterRunning,
                                onExpenseClick = { viewModel.handleIntent(TimelineIntent.ShowExpenseStatsDialog) },
                                onStepCardClick = onStepCardClick
                            )
                        }

                        if (state.groupedEntries.isEmpty()) {
                            item(key = "empty_state") {
                                EmptyStateView(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 64.dp)
                                )
                            }
                        } else {
                            state.groupedEntries.forEach { dateGroup ->
                                item(key = "header_${dateGroup.date}") {
                                    DateGroupHeader(
                                        date = dateGroup.date,
                                        entryCount = dateGroup.entryCount,
                                        expenseCount = dateGroup.expenseCount,
                                        isExpanded = dateGroup.isExpanded,
                                        onClick = { viewModel.handleIntent(TimelineIntent.ToggleDateGroup(dateGroup.date)) },
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                    )
                                }

                                item(key = "items_${dateGroup.date}") {
                                    AnimatedItemsColumn(
                                        items = dateGroup.items,
                                        isExpanded = dateGroup.isExpanded,
                                        onEntryClick = { entryId ->
                                            viewModel.handleIntent(TimelineIntent.EntryClicked(entryId))
                                        },
                                        onEntryDelete = { entryId ->
                                            viewModel.handleIntent(TimelineIntent.DeleteEntry(entryId))
                                        },
                                        onExpenseDelete = { expenseId ->
                                            viewModel.handleIntent(TimelineIntent.DeleteExpense(expenseId))
                                        },
                                        onToggleTask = { taskId, isCompleted ->
                                            viewModel.handleIntent(TimelineIntent.ToggleTaskCompletion(taskId, isCompleted))
                                        },
                                        onDeleteTask = { taskId ->
                                            viewModel.handleIntent(TimelineIntent.DeleteTask(taskId))
                                        },
                                        onVideoClick = { video ->
                                            viewModel.handleIntent(TimelineIntent.PlayVideo(video))
                                        },
                                        onDeleteVideo = { videoId ->
                                            viewModel.handleIntent(TimelineIntent.DeleteVideo(videoId))
                                        },
                                        onDeleteFood = { foodId ->
                                            viewModel.handleIntent(TimelineIntent.DeleteFood(foodId))
                                        },
                                        onTrackMapClick = { track ->
                                            viewModel.handleIntent(TimelineIntent.OpenTrackMap(track))
                                        },
                                        onDeleteTrack = { trackId ->
                                            viewModel.handleIntent(TimelineIntent.DeleteTrack(trackId))
                                        },
                                        isCardExpanded = { cardId -> state.isCardExpanded(cardId) },
                                        onToggleCardExpansion = { cardId ->
                                            viewModel.handleIntent(TimelineIntent.ToggleCardExpansion(cardId))
                                        }
                                    )
                                }

                                item(key = "spacer_${dateGroup.date}") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Editor Dialog
    if (state.showEditorDialog) {
        Dialog(
            onDismissRequest = { viewModel.handleIntent(TimelineIntent.CloseEditorDialog) },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            EditorScreen(
                entryId = state.editingEntryId,
                viewModelKey = state.editorDialogKey,
                onNavigateBack = { viewModel.handleIntent(TimelineIntent.CloseEditorDialog) }
            )
        }
    }

    // Action Choice Dialog
    if (state.showActionChoiceDialog) {
        ActionChoiceDialog(
            onAddEntry = { viewModel.handleIntent(TimelineIntent.AddEntryClicked) },
            onAddExpense = { viewModel.handleIntent(TimelineIntent.ShowAddExpenseDialog) },
            onAddTask = { viewModel.handleIntent(TimelineIntent.ShowAddTaskDialog) },
            onAddVideo = { viewModel.handleIntent(TimelineIntent.ShowAddVideoDialog) },
            onAddFood = { viewModel.handleIntent(TimelineIntent.ShowAddFoodDialog) },
            onToggleTracking = { viewModel.handleIntent(TimelineIntent.ToggleTracking) },
            isTracking = state.isTracking,
            onDismiss = { viewModel.handleIntent(TimelineIntent.HideActionChoiceDialog) }
        )
    }

    // Add Expense Dialog with its own ViewModel
    if (state.showAddExpenseDialog) {
        AddExpenseDialogWithViewModel(
            key = state.expenseDialogKey,
            onDismiss = { viewModel.handleIntent(TimelineIntent.HideAddExpenseDialog) },
            onSaveSuccess = { viewModel.handleIntent(TimelineIntent.HideAddExpenseDialog) }
        )
    }

    // Expense Stats Dialog
    if (state.showExpenseStatsDialog) {
        ExpenseStatsDialog(
            expenses = state.todayExpenses,
            date = LocalDate.now(),
            onDelete = { viewModel.handleIntent(TimelineIntent.DeleteExpense(it)) },
            onDismiss = { viewModel.handleIntent(TimelineIntent.HideExpenseStatsDialog) }
        )
    }

    // Add Task Dialog with its own ViewModel
    if (state.showAddTaskDialog) {
        AddTaskDialogWithViewModel(
            key = state.taskDialogKey,
            onDismiss = { viewModel.handleIntent(TimelineIntent.HideAddTaskDialog) },
            onSaveSuccess = { viewModel.handleIntent(TimelineIntent.HideAddTaskDialog) }
        )
    }

    // Add Video Dialog with its own ViewModel
    if (state.showAddVideoDialog) {
        AddVideoDialogWithViewModel(
            key = state.videoDialogKey,
            onDismiss = { viewModel.handleIntent(TimelineIntent.HideAddVideoDialog) },
            onSaveSuccess = { viewModel.handleIntent(TimelineIntent.HideAddVideoDialog) }
        )
    }

    // Video Player Dialog
    state.playingVideo?.let { video ->
        VideoPlayerDialog(
            video = video,
            onDismiss = { viewModel.handleIntent(TimelineIntent.CloseVideoPlayer) }
        )
    }

    // Add Food Dialog with its own ViewModel
    if (state.showAddFoodDialog) {
        AddFoodDialogWithViewModel(
            key = state.foodDialogKey,
            onDismiss = { viewModel.handleIntent(TimelineIntent.HideAddFoodDialog) },
            onSaveSuccess = { viewModel.handleIntent(TimelineIntent.HideAddFoodDialog) }
        )
    }

    // Full Screen Map Dialog
    state.fullMapTrack?.let { track ->
        if (state.showFullMapDialog) {
            FullScreenMapDialog(
                track = track,
                isTracking = state.isTracking,
                onToggleTracking = { viewModel.handleIntent(TimelineIntent.ToggleTracking) },
                onDismiss = { viewModel.handleIntent(TimelineIntent.CloseTrackMap) },
                titleText = stringResource(R.string.full_map),
                startTrackingText = stringResource(R.string.start_tracking),
                stopTrackingText = stringResource(R.string.stop_tracking),
                distanceLabel = stringResource(R.string.track_distance),
                durationLabel = stringResource(R.string.track_duration),
                speedLabel = stringResource(R.string.track_avg_speed),
                stepsLabel = stringResource(R.string.track_steps),
                kmUnit = stringResource(R.string.track_km),
                kmhUnit = stringResource(R.string.track_kmh)
            )
        }
    }
}

@Composable
private fun AddExpenseDialogWithViewModel(
    key: String,
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AddExpenseDialogViewModel = hiltViewModel(key = key)
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddExpenseDialogEffect.SaveSuccess -> onSaveSuccess()
            }
        }
    }

    AddExpenseDialog(
        amount = state.amount,
        selectedCategory = state.selectedCategory,
        description = state.description,
        onAmountChange = { viewModel.handleIntent(AddExpenseDialogIntent.AmountChanged(it)) },
        onCategoryChange = { viewModel.handleIntent(AddExpenseDialogIntent.CategoryChanged(it)) },
        onDescriptionChange = { viewModel.handleIntent(AddExpenseDialogIntent.DescriptionChanged(it)) },
        onSave = { viewModel.handleIntent(AddExpenseDialogIntent.Save) },
        onDismiss = onDismiss
    )
}

@Composable
private fun AddTaskDialogWithViewModel(
    key: String,
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AddTaskDialogViewModel = hiltViewModel(key = key)
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AddTaskDialogEffect.SaveSuccess -> onSaveSuccess()
            }
        }
    }

    AddTaskDialog(
        taskTitles = state.taskTitles,
        onTaskTitleChange = { index, title ->
            viewModel.handleIntent(AddTaskDialogIntent.TitleChanged(index, title))
        },
        onAddTaskField = { viewModel.handleIntent(AddTaskDialogIntent.AddField) },
        onRemoveTaskField = { index -> viewModel.handleIntent(AddTaskDialogIntent.RemoveField(index)) },
        onSave = { viewModel.handleIntent(AddTaskDialogIntent.Save) },
        onDismiss = onDismiss,
        dialogTitle = stringResource(R.string.add_tasks),
        titlePlaceholder = stringResource(R.string.task_title_placeholder),
        saveText = stringResource(R.string.add),
        cancelText = stringResource(R.string.cancel),
        addMoreText = stringResource(R.string.add_more_task)
    )
}

@Composable
private fun AddVideoDialogWithViewModel(
    key: String,
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AddVideoDialogViewModel = hiltViewModel(key = key)
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddVideoDialogEffect.SaveSuccess -> onSaveSuccess()
            }
        }
    }

    // Video picker launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            // Take persistable permission for gallery videos
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
                // Permission may not be available for all URIs
            }

            val durationMs = getVideoDuration(context, it)
            val thumbnailUri = extractVideoThumbnail(context, it)
            viewModel.handleIntent(AddVideoDialogIntent.VideoSelected(it, durationMs, thumbnailUri))
        }
    }

    // Camera launcher for recording video
    var videoUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && videoUri != null) {
            val durationMs = getVideoDuration(context, videoUri!!)
            val thumbnailUri = extractVideoThumbnail(context, videoUri!!)
            viewModel.handleIntent(AddVideoDialogIntent.VideoSelected(videoUri!!, durationMs, thumbnailUri))
        }
    }

    AddVideoDialog(
        selectedVideoUri = state.selectedVideoUri,
        thumbnailUri = state.thumbnailUri,
        videoDurationMs = state.videoDurationMs,
        videoTitle = state.videoTitle,
        isSaving = state.isSaving,
        onTitleChange = { viewModel.handleIntent(AddVideoDialogIntent.TitleChanged(it)) },
        onRecordVideo = {
            val file = java.io.File(context.cacheDir, "videos").apply { mkdirs() }
            val videoFile = java.io.File(file, "video_${System.currentTimeMillis()}.mp4")
            videoUri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                videoFile
            )
            cameraLauncher.launch(videoUri!!)
        },
        onPickFromGallery = {
            videoPickerLauncher.launch("video/*")
        },
        onSave = { viewModel.handleIntent(AddVideoDialogIntent.Save) },
        onDismiss = onDismiss,
        dialogTitle = stringResource(R.string.add_video),
        recordVideoText = stringResource(R.string.record_video),
        recordVideoDescription = stringResource(R.string.record_video_description),
        pickFromGalleryText = stringResource(R.string.choose_from_gallery),
        pickFromGalleryDescription = stringResource(R.string.choose_video_from_gallery_description),
        titlePlaceholder = stringResource(R.string.video_title_placeholder),
        saveText = stringResource(R.string.add),
        cancelText = stringResource(R.string.cancel)
    )
}

@Composable
private fun AddFoodDialogWithViewModel(
    key: String,
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AddFoodDialogViewModel = hiltViewModel(key = key)
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddFoodDialogEffect.SaveSuccess -> onSaveSuccess()
            }
        }
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            // Take persistable permission for gallery photos
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
                // Permission may not be available for all URIs
            }
            viewModel.handleIntent(AddFoodDialogIntent.PhotoSelected(it))
        }
    }

    // Camera launcher for taking photo
    var photoUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            viewModel.handleIntent(AddFoodDialogIntent.PhotoSelected(photoUri!!))
        }
    }

    AddFoodDialog(
        selectedPhotoUri = state.selectedPhotoUri,
        isAnalyzing = state.isAnalyzing,
        isDownloadingModel = state.isDownloadingModel,
        downloadProgress = state.downloadProgress,
        predictions = state.predictions,
        selectedPrediction = state.selectedPrediction,
        selectedServingSize = state.selectedServingSize,
        isSaving = state.isSaving,
        error = state.error,
        onTakePhoto = {
            val file = java.io.File(context.cacheDir, "food_photos").apply { mkdirs() }
            val photoFile = java.io.File(file, "food_${System.currentTimeMillis()}.jpg")
            photoUri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            cameraLauncher.launch(photoUri!!)
        },
        onPickFromGallery = {
            photoPickerLauncher.launch("image/*")
        },
        onPredictionSelected = { viewModel.handleIntent(AddFoodDialogIntent.PredictionSelected(it)) },
        onServingSizeSelected = { viewModel.handleIntent(AddFoodDialogIntent.ServingSizeSelected(it)) },
        onSave = { viewModel.handleIntent(AddFoodDialogIntent.Save) },
        onDismiss = onDismiss,
        onClearPhoto = { viewModel.handleIntent(AddFoodDialogIntent.ClearPhoto) },
        onRetryDownload = { viewModel.handleIntent(AddFoodDialogIntent.RetryDownload) },
        dialogTitle = stringResource(R.string.add_food),
        takePhotoText = stringResource(R.string.take_food_photo),
        takePhotoDescription = stringResource(R.string.take_food_photo_description),
        pickFromGalleryText = stringResource(R.string.pick_food_photo),
        pickFromGalleryDescription = stringResource(R.string.pick_food_photo_description),
        analyzingText = stringResource(R.string.analyzing_food),
        downloadingModelText = stringResource(R.string.downloading_model),
        recognizedAsText = stringResource(R.string.recognized_as),
        servingSizeText = stringResource(R.string.serving_size),
        caloriesText = stringResource(R.string.calories),
        saveText = stringResource(R.string.add),
        cancelText = stringResource(R.string.cancel),
        retryText = stringResource(R.string.retry)
    )
}

private fun getVideoDuration(context: android.content.Context, uri: android.net.Uri): Long {
    return try {
        val retriever = android.media.MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val durationStr = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        durationStr?.toLongOrNull() ?: 0L
    } catch (_: Exception) {
        0L
    }
}

private fun extractVideoThumbnail(context: android.content.Context, uri: android.net.Uri): String? {
    return try {
        val retriever = android.media.MediaMetadataRetriever()
        retriever.setDataSource(context, uri)

        // Get first frame (at 0 microseconds)
        val bitmap = retriever.getFrameAtTime(0, android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        retriever.release()

        if (bitmap != null) {
            // Save thumbnail to cache
            val thumbnailDir = java.io.File(context.cacheDir, "video_thumbnails").apply { mkdirs() }
            val thumbnailFile = java.io.File(thumbnailDir, "thumb_${System.currentTimeMillis()}.jpg")

            java.io.FileOutputStream(thumbnailFile).use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, out)
            }
            bitmap.recycle()

            thumbnailFile.absolutePath
        } else {
            null
        }
    } catch (_: Exception) {
        null
    }
}

@Composable
private fun TodaySummaryRow(
    expenses: List<ru.diaries.mydiaries.data.model.Expense>,
    steps: Int,
    isStepCounterRunning: Boolean,
    onExpenseClick: () -> Unit,
    onStepCardClick: () -> Unit = {}
) {
    val total = expenses.sumOf { it.amount }
    val numberFormat = NumberFormat.getInstance(Locale("ru", "RU"))
    val stepGoal = 10_000
    val progress = (steps.toFloat() / stepGoal).coerceIn(0f, 1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Expense card
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Terracotta.copy(alpha = 0.15f),
                    ambientColor = Terracotta.copy(alpha = 0.06f)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            onClick = onExpenseClick
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Accent stripe
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Terracotta, GoldenHoney)
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "\uD83D\uDCB0",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.today_expenses),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = formatCurrency(total),
                        style = NumberStyle,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // Category mini-chart row
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            MiniPieChart(
                                expenses = expenses,
                                size = 22.dp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.view_details),
                            style = MaterialTheme.typography.labelSmall,
                            color = Terracotta
                        )
                    }
                }
            }
        }

        // Steps card
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = SageGreen.copy(alpha = 0.15f),
                    ambientColor = SageGreen.copy(alpha = 0.06f)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            onClick = onStepCardClick
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Accent stripe
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(SageGreen, GoldenHoney)
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "\uD83D\uDEB6",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.steps_today),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = numberFormat.format(steps),
                        style = NumberStyle,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // Progress bar + label
                    Column(modifier = Modifier.padding(top = 6.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(SageGreen, GoldenHoney)
                                        )
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.step_goal_progress,
                                    numberFormat.format(steps),
                                    numberFormat.format(stepGoal)
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.view_hourly_chart),
                                style = MaterialTheme.typography.labelSmall,
                                color = SageGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    return format.format(amount)
}

@Composable
private fun getGreetingText(): String {
    val hour = java.time.LocalTime.now().hour
    return when (hour) {
        in 5..11 -> stringResource(R.string.greeting_morning)
        in 12..16 -> stringResource(R.string.greeting_afternoon)
        in 17..22 -> stringResource(R.string.greeting_evening)
        else -> stringResource(R.string.greeting_night)
    }
}

@Composable
private fun formatTodayDate(): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
        .withLocale(Locale.getDefault())
    return LocalDate.now().format(formatter)
}

@Composable
private fun AnimatedItemsColumn(
    items: List<TimelineItem>,
    isExpanded: Boolean,
    onEntryClick: (String) -> Unit,
    onEntryDelete: (String) -> Unit,
    onExpenseDelete: (String) -> Unit,
    onToggleTask: (String, Boolean) -> Unit = { _, _ -> },
    onDeleteTask: (String) -> Unit = {},
    onVideoClick: (Video) -> Unit = {},
    onDeleteVideo: (String) -> Unit = {},
    onDeleteFood: (String) -> Unit = {},
    onTrackMapClick: (DailyTrack) -> Unit = {},
    onDeleteTrack: (String) -> Unit = {},
    isCardExpanded: (String) -> Boolean = { true },
    onToggleCardExpansion: (String) -> Unit = {}
) {
    AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            ),
            expandFrom = Alignment.Top
        ) + fadeIn(
            animationSpec = tween(durationMillis = 300)
        ),
        exit = shrinkVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            shrinkTowards = Alignment.Top
        ) + fadeOut(
            animationSpec = tween(durationMillis = 200)
        )
    ) {
        Column {
            items.forEachIndexed { index, item ->
                key(item.id) {
                    AnimatedTimelineItem(
                        item = item,
                        index = index,
                        onEntryClick = onEntryClick,
                        onEntryDelete = onEntryDelete,
                        onExpenseDelete = onExpenseDelete,
                        onToggleTask = onToggleTask,
                        onDeleteTask = onDeleteTask,
                        onVideoClick = onVideoClick,
                        onDeleteVideo = onDeleteVideo,
                        onDeleteFood = onDeleteFood,
                        onTrackMapClick = onTrackMapClick,
                        onDeleteTrack = onDeleteTrack,
                        isCardExpanded = isCardExpanded,
                        onToggleCardExpansion = onToggleCardExpansion
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedTimelineItem(
    item: TimelineItem,
    index: Int,
    onEntryClick: (String) -> Unit,
    onEntryDelete: (String) -> Unit,
    onExpenseDelete: (String) -> Unit,
    onToggleTask: (String, Boolean) -> Unit = { _, _ -> },
    onDeleteTask: (String) -> Unit = {},
    onVideoClick: (Video) -> Unit = {},
    onDeleteVideo: (String) -> Unit = {},
    onDeleteFood: (String) -> Unit = {},
    onTrackMapClick: (DailyTrack) -> Unit = {},
    onDeleteTrack: (String) -> Unit = {},
    isCardExpanded: (String) -> Boolean = { true },
    onToggleCardExpansion: (String) -> Unit = {}
) {
    val staggerDelay = index * 50

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            animationSpec = tween(
                durationMillis = 400,
                delayMillis = staggerDelay
            ),
            initialOffsetY = { -40 }
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 350,
                delayMillis = staggerDelay
            )
        ),
        exit = slideOutVertically(
            animationSpec = tween(durationMillis = 200),
            targetOffsetY = { -20 }
        ) + fadeOut(
            animationSpec = tween(durationMillis = 150)
        )
    ) {
        when (item) {
            is TimelineItem.DiaryItem -> {
                DiaryEntryCard(
                    entry = item.entry,
                    onClick = { onEntryClick(item.entry.id) },
                    onDelete = { onEntryDelete(item.entry.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                )
            }
            is TimelineItem.ExpensesItem -> {
                val cardId = "expenses_${item.date}"
                DayExpensesCard(
                    expenses = item.expenses,
                    onDeleteExpense = onExpenseDelete,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                    isExpanded = isCardExpanded(cardId),
                    onToggleExpand = { onToggleCardExpansion(cardId) }
                )
            }
            is TimelineItem.TasksItem -> {
                val cardId = "tasks_${item.date}"
                DayTasksCard(
                    tasks = item.tasks,
                    onToggleTask = onToggleTask,
                    onDeleteTask = onDeleteTask,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                    isExpanded = isCardExpanded(cardId),
                    onToggleExpand = { onToggleCardExpansion(cardId) },
                    cardTitle = stringResource(R.string.tasks_for_day),
                    completedText = stringResource(R.string.task_completed_short)
                )
            }
            is TimelineItem.VideosItem -> {
                val cardId = "videos_${item.date}"
                DayVideosCard(
                    videos = item.videos,
                    onVideoClick = onVideoClick,
                    onDeleteVideo = onDeleteVideo,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                    isExpanded = isCardExpanded(cardId),
                    onToggleExpand = { onToggleCardExpansion(cardId) },
                    cardTitle = stringResource(R.string.videos_for_day)
                )
            }
            is TimelineItem.FoodItem -> {
                val cardId = "food_${item.date}"
                DayFoodCard(
                    foodEntries = item.foodEntries,
                    onDeleteFood = onDeleteFood,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                    isExpanded = isCardExpanded(cardId),
                    onToggleExpand = { onToggleCardExpansion(cardId) },
                    cardTitle = stringResource(R.string.food_for_day)
                )
            }
            is TimelineItem.TrackItem -> {
                val cardId = "track_${item.date}"
                DayTrackCard(
                    track = item.track,
                    onMapClick = { onTrackMapClick(item.track) },
                    onDelete = { onDeleteTrack(item.track.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                    isExpanded = isCardExpanded(cardId),
                    onToggleExpand = { onToggleCardExpansion(cardId) },
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
        }
    }
}
