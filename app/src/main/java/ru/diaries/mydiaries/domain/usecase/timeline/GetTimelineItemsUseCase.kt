package ru.diaries.mydiaries.domain.usecase.timeline

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
import ru.diaries.mydiaries.feature.video.data.model.Video
import ru.diaries.mydiaries.feature.video.data.repository.VideoRepository
import java.time.LocalDate
import javax.inject.Inject

data class TimelineData(
    val entries: List<DiaryEntry>,
    val expenses: List<Expense>,
    val tasks: List<Task>,
    val videos: List<Video>,
    val foodEntries: List<FoodEntry>,
    val todayExpenses: List<Expense>,
    val todayTasks: List<Task>,
    val todayVideos: List<Video>,
    val todayFoodEntries: List<FoodEntry>
)

class GetTimelineItemsUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val expenseRepository: ExpenseRepository,
    private val taskRepository: TaskRepository,
    private val videoRepository: VideoRepository,
    private val foodRepository: FoodRepository
) {
    operator fun invoke(): Flow<TimelineData> {
        return combine(
            diaryRepository.getEntries(),
            expenseRepository.getAllExpenses(),
            taskRepository.getAllTasks(),
            videoRepository.getAllVideos(),
            foodRepository.getAllFoodEntries()
        ) { entries, expenses, tasks, videos, foodEntries ->
            val today = LocalDate.now()
            val sortedEntries = entries.sortedByDescending { it.date }
            val todayExpenses = expenses.filter { it.date == today }
            val todayTasks = tasks.filter { it.date == today }
            val todayVideos = videos.filter { it.date == today }
            val todayFoodEntries = foodEntries.filter { it.date == today }

            TimelineData(
                entries = sortedEntries,
                expenses = expenses,
                tasks = tasks,
                videos = videos,
                foodEntries = foodEntries,
                todayExpenses = todayExpenses,
                todayTasks = todayTasks,
                todayVideos = todayVideos,
                todayFoodEntries = todayFoodEntries
            )
        }
    }
}
