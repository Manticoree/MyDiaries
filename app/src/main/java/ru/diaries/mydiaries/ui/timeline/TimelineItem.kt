package ru.diaries.mydiaries.ui.timeline

import ru.diaries.mydiaries.data.model.DiaryEntry
import ru.diaries.mydiaries.data.model.Expense
import ru.diaries.mydiaries.feature.food.data.model.FoodEntry
import ru.diaries.mydiaries.feature.todo.data.model.Task
import ru.diaries.mydiaries.feature.track.data.model.DailyTrack
import ru.diaries.mydiaries.feature.video.data.model.Video
import java.time.LocalDate

sealed class TimelineItem {
    abstract val id: String
    abstract val date: LocalDate

    data class DiaryItem(val entry: DiaryEntry) : TimelineItem() {
        override val id: String get() = "diary_${entry.id}"
        override val date: LocalDate get() = entry.date
    }

    data class ExpensesItem(val expenses: List<Expense>) : TimelineItem() {
        override val id: String get() = "expenses_${expenses.firstOrNull()?.date ?: LocalDate.now()}"
        override val date: LocalDate get() = expenses.firstOrNull()?.date ?: LocalDate.now()
        val total: Double get() = expenses.sumOf { it.amount }
    }

    data class TasksItem(val tasks: List<Task>) : TimelineItem() {
        override val id: String get() = "tasks_${tasks.firstOrNull()?.date ?: LocalDate.now()}"
        override val date: LocalDate get() = tasks.firstOrNull()?.date ?: LocalDate.now()
        val completedCount: Int get() = tasks.count { it.isCompleted }
        val totalCount: Int get() = tasks.size
    }

    data class VideosItem(val videos: List<Video>) : TimelineItem() {
        override val id: String get() = "videos_${videos.firstOrNull()?.date ?: LocalDate.now()}"
        override val date: LocalDate get() = videos.firstOrNull()?.date ?: LocalDate.now()
        val totalCount: Int get() = videos.size
    }

    data class FoodItem(val foodEntries: List<FoodEntry>) : TimelineItem() {
        override val id: String get() = "food_${foodEntries.firstOrNull()?.date ?: LocalDate.now()}"
        override val date: LocalDate get() = foodEntries.firstOrNull()?.date ?: LocalDate.now()
        val totalCalories: Int get() = foodEntries.sumOf { it.actualCalories }
        val totalCount: Int get() = foodEntries.size
    }

    data class TrackItem(val track: DailyTrack) : TimelineItem() {
        override val id: String get() = "track_${track.date}"
        override val date: LocalDate get() = track.date
    }
}
