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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ChevronRight
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
import ru.diaries.mydiaries.ui.task.AddTaskDialogEffect
import ru.diaries.mydiaries.ui.task.AddTaskDialogIntent
import ru.diaries.mydiaries.ui.task.AddTaskDialogViewModel
import ru.diaries.mydiaries.feature.todo.ui.AddTaskDialog
import ru.diaries.mydiaries.feature.todo.ui.DayTasksCard
import ru.diaries.mydiaries.feature.food.data.model.FoodEntry
import ru.diaries.mydiaries.feature.food.ui.AddFoodDialog
import ru.diaries.mydiaries.feature.food.ui.DayFoodCard
import ru.diaries.mydiaries.feature.video.data.model.Video
import ru.diaries.mydiaries.feature.video.ui.AddVideoDialog
import ru.diaries.mydiaries.feature.video.ui.DayVideosCard
import ru.diaries.mydiaries.feature.video.ui.VideoPlayerDialog
import ru.diaries.mydiaries.ui.food.AddFoodDialogEffect
import ru.diaries.mydiaries.ui.food.AddFoodDialogIntent
import ru.diaries.mydiaries.ui.food.AddFoodDialogViewModel
import ru.diaries.mydiaries.ui.video.AddVideoDialogEffect
import ru.diaries.mydiaries.ui.video.AddVideoDialogIntent
import ru.diaries.mydiaries.ui.video.AddVideoDialogViewModel
import ru.diaries.mydiaries.ui.theme.GoldenHoney
import ru.diaries.mydiaries.ui.theme.GreetingStyle
import ru.diaries.mydiaries.ui.theme.NumberLargeStyle
import ru.diaries.mydiaries.ui.theme.Terracotta
import ru.diaries.mydiaries.ui.theme.WarmBrown
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.home_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
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
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                        item(key = "expense_card") {
                            TodayExpenseCard(
                                expenses = state.todayExpenses,
                                onClick = { viewModel.handleIntent(TimelineIntent.ShowExpenseStatsDialog) }
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
                                        isCardExpanded = { cardId -> state.isCardExpanded(cardId) },
                                        onToggleCardExpansion = { cardId ->
                                            viewModel.handleIntent(TimelineIntent.ToggleCardExpansion(cardId))
                                        }
                                    )
                                }

                                item(key = "spacer_${dateGroup.date}") {
                                    Spacer(modifier = Modifier.height(12.dp))
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
        key(state.editorDialogKey) {
            Dialog(
                onDismissRequest = { viewModel.handleIntent(TimelineIntent.CloseEditorDialog) },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                EditorScreen(
                    entryId = state.editingEntryId,
                    onNavigateBack = { viewModel.handleIntent(TimelineIntent.CloseEditorDialog) }
                )
            }
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
            onDismiss = { viewModel.handleIntent(TimelineIntent.HideActionChoiceDialog) }
        )
    }

    // Add Expense Dialog with its own ViewModel
    if (state.showAddExpenseDialog) {
        key(state.expenseDialogKey) {
            AddExpenseDialogWithViewModel(
                onDismiss = { viewModel.handleIntent(TimelineIntent.HideAddExpenseDialog) },
                onSaveSuccess = { viewModel.handleIntent(TimelineIntent.HideAddExpenseDialog) }
            )
        }
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
        key(state.taskDialogKey) {
            AddTaskDialogWithViewModel(
                onDismiss = { viewModel.handleIntent(TimelineIntent.HideAddTaskDialog) },
                onSaveSuccess = { viewModel.handleIntent(TimelineIntent.HideAddTaskDialog) }
            )
        }
    }

    // Add Video Dialog with its own ViewModel
    if (state.showAddVideoDialog) {
        key(state.videoDialogKey) {
            AddVideoDialogWithViewModel(
                onDismiss = { viewModel.handleIntent(TimelineIntent.HideAddVideoDialog) },
                onSaveSuccess = { viewModel.handleIntent(TimelineIntent.HideAddVideoDialog) }
            )
        }
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
        key(state.foodDialogKey) {
            AddFoodDialogWithViewModel(
                onDismiss = { viewModel.handleIntent(TimelineIntent.HideAddFoodDialog) },
                onSaveSuccess = { viewModel.handleIntent(TimelineIntent.HideAddFoodDialog) }
            )
        }
    }
}

@Composable
private fun AddExpenseDialogWithViewModel(
    viewModel: AddExpenseDialogViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit
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
    viewModel: AddTaskDialogViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit
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
    viewModel: AddVideoDialogViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit
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
    viewModel: AddFoodDialogViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Clear state when dialog opens
    LaunchedEffect(Unit) {
        viewModel.handleIntent(AddFoodDialogIntent.Clear)
    }

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
private fun TodayExpenseCard(
    expenses: List<ru.diaries.mydiaries.data.model.Expense>,
    onClick: () -> Unit
) {
    val total = expenses.sumOf { it.amount }
    val greeting = getGreetingByTimeOfDay()

    val headerGradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = WarmBrown.copy(alpha = 0.15f),
                ambientColor = WarmBrown.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        onClick = onClick
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerGradient)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = greeting,
                    style = GreetingStyle,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            spotColor = WarmBrown.copy(alpha = 0.1f)
                        )
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    MiniPieChart(
                        expenses = expenses,
                        size = 48.dp
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.today_expenses),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatCurrency(total),
                        style = NumberLargeStyle,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.view_details),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getGreetingByTimeOfDay(): String {
    val currentHour = LocalTime.now().hour
    return when (currentHour) {
        in 5..11 -> stringResource(R.string.greeting_morning)
        in 12..17 -> stringResource(R.string.greeting_afternoon)
        in 18..22 -> stringResource(R.string.greeting_evening)
        else -> stringResource(R.string.greeting_night)
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    return format.format(amount)
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
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            is TimelineItem.ExpensesItem -> {
                val cardId = "expenses_${item.date}"
                DayExpensesCard(
                    expenses = item.expenses,
                    onDeleteExpense = onExpenseDelete,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    isExpanded = isCardExpanded(cardId),
                    onToggleExpand = { onToggleCardExpansion(cardId) },
                    cardTitle = stringResource(R.string.food_for_day)
                )
            }
        }
    }
}
