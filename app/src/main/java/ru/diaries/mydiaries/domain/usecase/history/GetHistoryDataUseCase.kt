package ru.diaries.mydiaries.domain.usecase.history

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.diaries.mydiaries.data.model.DiaryEntry
import ru.diaries.mydiaries.data.model.Expense
import ru.diaries.mydiaries.data.repository.DiaryRepository
import ru.diaries.mydiaries.data.repository.ExpenseRepository
import ru.diaries.mydiaries.feature.food.data.model.FoodEntry
import ru.diaries.mydiaries.feature.food.data.repository.FoodRepository
import ru.diaries.mydiaries.feature.todo.data.model.Task
import ru.diaries.mydiaries.feature.todo.data.repository.TaskRepository
import ru.diaries.mydiaries.feature.track.data.model.DailyTrack
import ru.diaries.mydiaries.feature.track.data.repository.TrackRepository
import ru.diaries.mydiaries.feature.video.data.model.Video
import ru.diaries.mydiaries.feature.video.data.repository.VideoRepository
import java.time.LocalDate
import javax.inject.Inject

data class DaySummary(
    val date: LocalDate,
    val entries: List<DiaryEntry>,
    val expenses: List<Expense>,
    val tasks: List<Task>,
    val videos: List<Video>,
    val foodEntries: List<FoodEntry>,
    val track: DailyTrack?
) {
    val entryCount: Int get() = entries.size
    val totalExpenses: Double get() = expenses.sumOf { it.amount }
    val taskCount: Int get() = tasks.size
    val totalSteps: Int get() = track?.steps ?: 0
    val totalCalories: Int get() = foodEntries.sumOf { it.actualCalories }
    val distanceKm: Double get() = (track?.distanceMeters ?: 0.0) / 1000.0
    val previewText: String? get() = entries.firstOrNull()?.let {
        it.title.ifBlank { it.content.take(80) }
    }
}

data class HistoryData(
    val days: List<DaySummary>,
    val totalDays: Int,
    val totalEntries: Int,
    val totalSteps: Int,
    val totalDistanceKm: Double,
    val totalExpenses: Double,
    val totalCalories: Int
)

class GetHistoryDataUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val expenseRepository: ExpenseRepository,
    private val taskRepository: TaskRepository,
    private val videoRepository: VideoRepository,
    private val foodRepository: FoodRepository,
    private val trackRepository: TrackRepository
) {
    operator fun invoke(): Flow<HistoryData> {
        val firstFive = combine(
            diaryRepository.getEntries(),
            expenseRepository.getAllExpenses(),
            taskRepository.getAllTasks(),
            videoRepository.getAllVideos(),
            foodRepository.getAllFoodEntries()
        ) { entries, expenses, tasks, videos, foodEntries ->
            FiveFlowResult(entries, expenses, tasks, videos, foodEntries)
        }

        return combine(firstFive, trackRepository.getAllTracks()) { five, tracks ->
            val allDates = (
                five.entries.map { it.date } +
                five.expenses.map { it.date } +
                five.tasks.map { it.date } +
                five.videos.map { it.date } +
                five.foodEntries.map { it.date } +
                tracks.map { it.date }
            ).distinct().sortedDescending()

            val days = allDates.map { date ->
                DaySummary(
                    date = date,
                    entries = five.entries.filter { it.date == date },
                    expenses = five.expenses.filter { it.date == date },
                    tasks = five.tasks.filter { it.date == date },
                    videos = five.videos.filter { it.date == date },
                    foodEntries = five.foodEntries.filter { it.date == date },
                    track = tracks.find { it.date == date }
                )
            }

            HistoryData(
                days = days,
                totalDays = allDates.size,
                totalEntries = five.entries.size,
                totalSteps = tracks.sumOf { it.steps },
                totalDistanceKm = tracks.sumOf { it.distanceMeters } / 1000.0,
                totalExpenses = five.expenses.sumOf { it.amount },
                totalCalories = five.foodEntries.sumOf { it.actualCalories }
            )
        }
    }

    private data class FiveFlowResult(
        val entries: List<DiaryEntry>,
        val expenses: List<Expense>,
        val tasks: List<Task>,
        val videos: List<Video>,
        val foodEntries: List<FoodEntry>
    )
}
