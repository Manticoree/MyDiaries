package ru.diaries.mydiaries.ui.timeline

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.data.model.DiaryEntry
import ru.diaries.mydiaries.data.model.Expense
import ru.diaries.mydiaries.data.repository.DiaryRepository
import ru.diaries.mydiaries.data.repository.ExpenseRepository
import ru.diaries.mydiaries.domain.usecase.timeline.GetTimelineItemsUseCase
import ru.diaries.mydiaries.feature.food.data.model.FoodEntry
import ru.diaries.mydiaries.feature.food.data.repository.FoodRepository
import ru.diaries.mydiaries.feature.todo.data.model.Task
import ru.diaries.mydiaries.feature.todo.data.repository.TaskRepository
import ru.diaries.mydiaries.feature.track.data.model.DailyTrack
import ru.diaries.mydiaries.feature.track.data.repository.TrackRepository
import ru.diaries.mydiaries.feature.video.data.model.Video
import ru.diaries.mydiaries.feature.video.data.repository.VideoRepository
import ru.diaries.mydiaries.service.LocationTrackingService
import ru.diaries.mydiaries.service.StepCounterService
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val application: Application,
    private val getTimelineItemsUseCase: GetTimelineItemsUseCase,
    private val diaryRepository: DiaryRepository,
    private val expenseRepository: ExpenseRepository,
    private val taskRepository: TaskRepository,
    private val videoRepository: VideoRepository,
    private val foodRepository: FoodRepository,
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TimelineState())
    val state: StateFlow<TimelineState> = _state.asStateFlow()

    init {
        handleIntent(TimelineIntent.LoadEntries)
        observeTrackingState()
        observeStepCounter()
        startStepCounter()
    }

    private fun observeTrackingState() {
        viewModelScope.launch {
            LocationTrackingService.isTracking.collect { tracking ->
                _state.update { it.copy(isTracking = tracking) }
            }
        }
    }

    private fun observeStepCounter() {
        viewModelScope.launch {
            StepCounterService.todaySteps.collect { steps ->
                _state.update { it.copy(todaySteps = steps) }
            }
        }
        viewModelScope.launch {
            StepCounterService.isRunning.collect { running ->
                _state.update { it.copy(isStepCounterRunning = running) }
            }
        }
    }

    private fun startStepCounter() {
        if (!StepCounterService.isRunning.value) {
            StepCounterService.start(application)
        }
    }

    fun handleIntent(intent: TimelineIntent) {
        when (intent) {
            is TimelineIntent.LoadEntries -> loadTimeline()

            is TimelineIntent.DeleteEntry -> deleteEntry(intent.entryId)
            is TimelineIntent.EntryClicked -> openEditor(intent.entryId)
            is TimelineIntent.AddEntryClicked -> openEditor(null)
            is TimelineIntent.CloseEditorDialog -> closeEditor()

            is TimelineIntent.ToggleDateGroup -> toggleDateGroup(intent.date)

            is TimelineIntent.ShowActionChoiceDialog -> showActionChoiceDialog()
            is TimelineIntent.HideActionChoiceDialog -> hideActionChoiceDialog()

            is TimelineIntent.ShowAddExpenseDialog -> showAddExpenseDialog()
            is TimelineIntent.HideAddExpenseDialog -> hideAddExpenseDialog()
            is TimelineIntent.ShowExpenseStatsDialog -> showExpenseStatsDialog()
            is TimelineIntent.HideExpenseStatsDialog -> hideExpenseStatsDialog()
            is TimelineIntent.DeleteExpense -> deleteExpense(intent.id)

            is TimelineIntent.ShowAddTaskDialog -> showAddTaskDialog()
            is TimelineIntent.HideAddTaskDialog -> hideAddTaskDialog()
            is TimelineIntent.ToggleTaskCompletion -> toggleTaskCompletion(intent.taskId, intent.isCompleted)
            is TimelineIntent.DeleteTask -> deleteTask(intent.id)

            is TimelineIntent.ShowAddVideoDialog -> showAddVideoDialog()
            is TimelineIntent.HideAddVideoDialog -> hideAddVideoDialog()
            is TimelineIntent.PlayVideo -> playVideo(intent.video)
            is TimelineIntent.CloseVideoPlayer -> closeVideoPlayer()
            is TimelineIntent.DeleteVideo -> deleteVideo(intent.id)

            is TimelineIntent.ShowAddFoodDialog -> showAddFoodDialog()
            is TimelineIntent.HideAddFoodDialog -> hideAddFoodDialog()
            is TimelineIntent.DeleteFood -> deleteFood(intent.id)

            is TimelineIntent.ToggleCardExpansion -> toggleCardExpansion(intent.cardId)

            is TimelineIntent.ToggleTracking -> toggleTracking()
            is TimelineIntent.OpenTrackMap -> openTrackMap(intent.track)
            is TimelineIntent.CloseTrackMap -> closeTrackMap()
            is TimelineIntent.DeleteTrack -> deleteTrack(intent.id)
        }
    }

    private fun loadTimeline() {
        viewModelScope.launch {
            getTimelineItemsUseCase()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { data ->
                    val grouped = groupItemsByDate(data.entries, data.expenses, data.tasks, data.videos, data.foodEntries, data.tracks)
                    _state.update {
                        it.copy(
                            entries = data.entries,
                            groupedEntries = grouped,
                            todayExpenses = data.todayExpenses,
                            todayTasks = data.todayTasks,
                            todayVideos = data.todayVideos,
                            todayFoodEntries = data.todayFoodEntries,
                            todayTrack = data.todayTrack,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun groupItemsByDate(
        entries: List<DiaryEntry>,
        expenses: List<Expense>,
        tasks: List<Task>,
        videos: List<Video>,
        foodEntries: List<FoodEntry>,
        tracks: List<DailyTrack>
    ): List<DateGroup> {
        val allDates = (entries.map { it.date } + expenses.map { it.date } + tasks.map { it.date } + videos.map { it.date } + foodEntries.map { it.date } + tracks.map { it.date }).distinct()
        val today = LocalDate.now()

        return allDates.map { date ->
            val diaryItems = entries
                .filter { it.date == date }
                .map { TimelineItem.DiaryItem(it) }

            val dayExpenses = expenses.filter { it.date == date }
            val expensesItem = if (dayExpenses.isNotEmpty()) {
                listOf(TimelineItem.ExpensesItem(dayExpenses))
            } else {
                emptyList()
            }

            val dayTasks = tasks.filter { it.date == date }
            val tasksItem = if (dayTasks.isNotEmpty()) {
                listOf(TimelineItem.TasksItem(dayTasks))
            } else {
                emptyList()
            }

            val dayVideos = videos.filter { it.date == date }
            val videosItem = if (dayVideos.isNotEmpty()) {
                listOf(TimelineItem.VideosItem(dayVideos))
            } else {
                emptyList()
            }

            val dayFoodEntries = foodEntries.filter { it.date == date }
            val foodItem = if (dayFoodEntries.isNotEmpty()) {
                listOf(TimelineItem.FoodItem(dayFoodEntries))
            } else {
                emptyList()
            }

            val dayTrack = tracks.find { it.date == date }
            val trackItem = if (dayTrack != null) {
                listOf(TimelineItem.TrackItem(dayTrack))
            } else {
                emptyList()
            }

            val existingGroup = _state.value.groupedEntries.find { it.date == date }
            DateGroup(
                date = date,
                items = trackItem + tasksItem + foodItem + videosItem + diaryItems + expensesItem,
                isExpanded = existingGroup?.isExpanded ?: (date == today)
            )
        }.sortedByDescending { it.date }
    }

    private fun deleteEntry(entryId: String) {
        viewModelScope.launch {
            diaryRepository.deleteEntry(entryId)
        }
    }

    private fun openEditor(entryId: String?) {
        _state.update {
            it.copy(
                showEditorDialog = true,
                editingEntryId = entryId,
                editorDialogKey = if (entryId == null) {
                    "new_${System.currentTimeMillis()}"
                } else {
                    "edit_$entryId"
                }
            )
        }
    }

    private fun closeEditor() {
        _state.update {
            it.copy(
                showEditorDialog = false,
                editingEntryId = null,
                editorDialogKey = ""
            )
        }
    }

    private fun toggleDateGroup(date: LocalDate) {
        _state.update { currentState ->
            currentState.copy(
                groupedEntries = currentState.groupedEntries.map { group ->
                    if (group.date == date) {
                        group.copy(isExpanded = !group.isExpanded)
                    } else {
                        group
                    }
                }
            )
        }
    }

    private fun showActionChoiceDialog() {
        _state.update { it.copy(showActionChoiceDialog = true) }
    }

    private fun hideActionChoiceDialog() {
        _state.update { it.copy(showActionChoiceDialog = false) }
    }

    private fun showAddExpenseDialog() {
        _state.update {
            it.copy(
                showAddExpenseDialog = true,
                expenseDialogKey = "expense_${System.currentTimeMillis()}"
            )
        }
    }

    private fun hideAddExpenseDialog() {
        _state.update { it.copy(showAddExpenseDialog = false, expenseDialogKey = "") }
    }

    private fun showExpenseStatsDialog() {
        _state.update { it.copy(showExpenseStatsDialog = true) }
    }

    private fun hideExpenseStatsDialog() {
        _state.update { it.copy(showExpenseStatsDialog = false) }
    }

    private fun deleteExpense(id: String) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(id)
        }
    }

    private fun showAddTaskDialog() {
        _state.update {
            it.copy(
                showAddTaskDialog = true,
                taskDialogKey = "task_${System.currentTimeMillis()}"
            )
        }
    }

    private fun hideAddTaskDialog() {
        _state.update { it.copy(showAddTaskDialog = false, taskDialogKey = "") }
    }

    private fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(taskId, isCompleted)
        }
    }

    private fun deleteTask(id: String) {
        viewModelScope.launch {
            taskRepository.deleteTask(id)
        }
    }

    private fun showAddVideoDialog() {
        _state.update {
            it.copy(
                showAddVideoDialog = true,
                videoDialogKey = "video_${System.currentTimeMillis()}"
            )
        }
    }

    private fun hideAddVideoDialog() {
        _state.update { it.copy(showAddVideoDialog = false, videoDialogKey = "") }
    }

    private fun playVideo(video: Video) {
        _state.update { it.copy(playingVideo = video) }
    }

    private fun closeVideoPlayer() {
        _state.update { it.copy(playingVideo = null) }
    }

    private fun deleteVideo(id: String) {
        viewModelScope.launch {
            videoRepository.deleteVideo(id)
        }
    }

    private fun showAddFoodDialog() {
        _state.update {
            it.copy(
                showAddFoodDialog = true,
                foodDialogKey = "food_${System.currentTimeMillis()}"
            )
        }
    }

    private fun hideAddFoodDialog() {
        _state.update { it.copy(showAddFoodDialog = false, foodDialogKey = "") }
    }

    private fun deleteFood(id: String) {
        viewModelScope.launch {
            foodRepository.deleteFoodEntry(id)
        }
    }

    private fun toggleCardExpansion(cardId: String) {
        _state.update { currentState ->
            val currentExpanded = currentState.isCardExpanded(cardId)
            currentState.copy(
                cardExpandedStates = currentState.cardExpandedStates + (cardId to !currentExpanded)
            )
        }
    }

    private fun toggleTracking() {
        if (_state.value.isTracking) {
            LocationTrackingService.stop(application)
        } else {
            LocationTrackingService.start(application)
        }
    }

    private fun openTrackMap(track: DailyTrack) {
        _state.update { it.copy(showFullMapDialog = true, fullMapTrack = track) }
    }

    private fun closeTrackMap() {
        _state.update { it.copy(showFullMapDialog = false, fullMapTrack = null) }
    }

    private fun deleteTrack(id: String) {
        viewModelScope.launch {
            trackRepository.deleteTrack(id)
        }
    }
}
