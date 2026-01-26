package ru.diaries.mydiaries.ui.timeline

import ru.diaries.mydiaries.data.model.DiaryEntry
import ru.diaries.mydiaries.data.model.Expense
import ru.diaries.mydiaries.feature.food.data.model.FoodEntry
import ru.diaries.mydiaries.feature.todo.data.model.Task
import ru.diaries.mydiaries.feature.video.data.model.Video

data class TimelineState(
    // Timeline data
    val entries: List<DiaryEntry> = emptyList(),
    val groupedEntries: List<DateGroup> = emptyList(),
    val todayExpenses: List<Expense> = emptyList(),
    val todayTasks: List<Task> = emptyList(),
    val todayVideos: List<Video> = emptyList(),
    val todayFoodEntries: List<FoodEntry> = emptyList(),

    // Card expansion states (key: "type_date", e.g. "tasks_2024-01-15")
    val cardExpandedStates: Map<String, Boolean> = emptyMap(),

    // Loading
    val isLoading: Boolean = true,
    val error: String? = null,

    // Dialog visibility only (NO form fields!)
    val showEditorDialog: Boolean = false,
    val editingEntryId: String? = null,
    val editorDialogKey: String = "",
    val showActionChoiceDialog: Boolean = false,
    val showAddExpenseDialog: Boolean = false,
    val expenseDialogKey: String = "",
    val showAddTaskDialog: Boolean = false,
    val taskDialogKey: String = "",
    val showExpenseStatsDialog: Boolean = false,
    val showAddVideoDialog: Boolean = false,
    val videoDialogKey: String = "",
    val playingVideo: Video? = null,
    val showAddFoodDialog: Boolean = false,
    val foodDialogKey: String = ""
) {
    fun isCardExpanded(cardId: String): Boolean = cardExpandedStates[cardId] ?: true
}
