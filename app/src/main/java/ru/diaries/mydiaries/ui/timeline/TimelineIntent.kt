package ru.diaries.mydiaries.ui.timeline

import ru.diaries.mydiaries.feature.video.data.model.Video
import java.time.LocalDate

sealed class TimelineIntent {
    // Loading
    data object LoadEntries : TimelineIntent()

    // Diary actions
    data class DeleteEntry(val entryId: String) : TimelineIntent()
    data class EntryClicked(val entryId: String) : TimelineIntent()
    data object AddEntryClicked : TimelineIntent()
    data object CloseEditorDialog : TimelineIntent()

    // Date group
    data class ToggleDateGroup(val date: LocalDate) : TimelineIntent()

    // Action Choice Dialog
    data object ShowActionChoiceDialog : TimelineIntent()
    data object HideActionChoiceDialog : TimelineIntent()

    // Expense dialog visibility
    data object ShowAddExpenseDialog : TimelineIntent()
    data object HideAddExpenseDialog : TimelineIntent()
    data object ShowExpenseStatsDialog : TimelineIntent()
    data object HideExpenseStatsDialog : TimelineIntent()
    data class DeleteExpense(val id: String) : TimelineIntent()

    // Task dialog visibility
    data object ShowAddTaskDialog : TimelineIntent()
    data object HideAddTaskDialog : TimelineIntent()
    data class ToggleTaskCompletion(val taskId: String, val isCompleted: Boolean) : TimelineIntent()
    data class DeleteTask(val id: String) : TimelineIntent()

    // Video dialog visibility
    data object ShowAddVideoDialog : TimelineIntent()
    data object HideAddVideoDialog : TimelineIntent()
    data class PlayVideo(val video: Video) : TimelineIntent()
    data object CloseVideoPlayer : TimelineIntent()
    data class DeleteVideo(val id: String) : TimelineIntent()

    // Food dialog visibility
    data object ShowAddFoodDialog : TimelineIntent()
    data object HideAddFoodDialog : TimelineIntent()
    data class DeleteFood(val id: String) : TimelineIntent()

    // Card expansion
    data class ToggleCardExpansion(val cardId: String) : TimelineIntent()

    // Track actions
    data object ToggleTracking : TimelineIntent()
    data class OpenTrackMap(val track: ru.diaries.mydiaries.feature.track.data.model.DailyTrack) : TimelineIntent()
    data object CloseTrackMap : TimelineIntent()
    data class DeleteTrack(val id: String) : TimelineIntent()
}
