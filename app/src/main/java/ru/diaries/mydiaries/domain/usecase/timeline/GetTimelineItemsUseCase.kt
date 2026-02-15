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
import ru.diaries.mydiaries.feature.track.data.model.DailyTrack
import ru.diaries.mydiaries.feature.track.data.repository.TrackRepository
import ru.diaries.mydiaries.feature.video.data.model.Video
import ru.diaries.mydiaries.feature.video.data.repository.VideoRepository
import ru.diaries.mydiaries.feature.workout.data.model.Workout
import ru.diaries.mydiaries.feature.workout.data.repository.WorkoutRepository
import java.time.LocalDate
import javax.inject.Inject

data class TimelineData(
    val entries: List<DiaryEntry>,
    val expenses: List<Expense>,
    val tasks: List<Task>,
    val videos: List<Video>,
    val foodEntries: List<FoodEntry>,
    val tracks: List<DailyTrack>,
    val workouts: List<Workout>,
    val todayExpenses: List<Expense>,
    val todayTasks: List<Task>,
    val todayVideos: List<Video>,
    val todayFoodEntries: List<FoodEntry>,
    val todayTrack: DailyTrack?,
    val todayWorkouts: List<Workout>
)

class GetTimelineItemsUseCase @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val expenseRepository: ExpenseRepository,
    private val taskRepository: TaskRepository,
    private val videoRepository: VideoRepository,
    private val foodRepository: FoodRepository,
    private val trackRepository: TrackRepository,
    private val workoutRepository: WorkoutRepository
) {
    operator fun invoke(): Flow<TimelineData> {
        val firstFive = combine(
            diaryRepository.getEntries(),
            expenseRepository.getAllExpenses(),
            taskRepository.getAllTasks(),
            videoRepository.getAllVideos(),
            foodRepository.getAllFoodEntries()
        ) { entries, expenses, tasks, videos, foodEntries ->
            FiveFlowResult(entries, expenses, tasks, videos, foodEntries)
        }

        val secondTwo = combine(
            trackRepository.getAllTracks(),
            workoutRepository.getAllWorkouts()
        ) { tracks, workouts ->
            TwoFlowResult(tracks, workouts)
        }

        return combine(firstFive, secondTwo) { five, two ->
            val today = LocalDate.now()
            val sortedEntries = five.entries.sortedByDescending { it.date }

            TimelineData(
                entries = sortedEntries,
                expenses = five.expenses,
                tasks = five.tasks,
                videos = five.videos,
                foodEntries = five.foodEntries,
                tracks = two.tracks,
                workouts = two.workouts,
                todayExpenses = five.expenses.filter { it.date == today },
                todayTasks = five.tasks.filter { it.date == today },
                todayVideos = five.videos.filter { it.date == today },
                todayFoodEntries = five.foodEntries.filter { it.date == today },
                todayTrack = two.tracks.find { it.date == today },
                todayWorkouts = two.workouts.filter { it.date == today }
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

    private data class TwoFlowResult(
        val tracks: List<DailyTrack>,
        val workouts: List<Workout>
    )
}
