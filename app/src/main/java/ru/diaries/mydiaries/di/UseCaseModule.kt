package ru.diaries.mydiaries.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import ru.diaries.mydiaries.data.repository.DiaryRepository
import ru.diaries.mydiaries.data.repository.ExpenseRepository
import ru.diaries.mydiaries.domain.usecase.expense.SaveExpenseUseCase
import ru.diaries.mydiaries.feature.food.domain.usecase.SaveFoodUseCase
import ru.diaries.mydiaries.feature.todo.domain.usecase.SaveTasksUseCase
import ru.diaries.mydiaries.domain.usecase.history.GetHistoryDataUseCase
import ru.diaries.mydiaries.domain.usecase.timeline.GetTimelineItemsUseCase
import ru.diaries.mydiaries.domain.usecase.statistics.GetStatisticsDataUseCase
import ru.diaries.mydiaries.feature.video.domain.usecase.SaveVideoUseCase
import ru.diaries.mydiaries.feature.food.data.repository.FoodRepository
import ru.diaries.mydiaries.feature.todo.data.repository.TaskRepository
import ru.diaries.mydiaries.feature.track.data.repository.TrackRepository
import ru.diaries.mydiaries.feature.video.data.repository.VideoRepository
import ru.diaries.mydiaries.feature.workout.data.repository.WorkoutRepository

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideGetTimelineItemsUseCase(
        diaryRepository: DiaryRepository,
        expenseRepository: ExpenseRepository,
        taskRepository: TaskRepository,
        videoRepository: VideoRepository,
        foodRepository: FoodRepository,
        trackRepository: TrackRepository,
        workoutRepository: WorkoutRepository
    ): GetTimelineItemsUseCase {
        return GetTimelineItemsUseCase(diaryRepository, expenseRepository, taskRepository, videoRepository, foodRepository, trackRepository, workoutRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetHistoryDataUseCase(
        diaryRepository: DiaryRepository,
        expenseRepository: ExpenseRepository,
        taskRepository: TaskRepository,
        videoRepository: VideoRepository,
        foodRepository: FoodRepository,
        trackRepository: TrackRepository
    ): GetHistoryDataUseCase {
        return GetHistoryDataUseCase(diaryRepository, expenseRepository, taskRepository, videoRepository, foodRepository, trackRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSaveExpenseUseCase(
        expenseRepository: ExpenseRepository
    ): SaveExpenseUseCase {
        return SaveExpenseUseCase(expenseRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSaveTasksUseCase(
        taskRepository: TaskRepository
    ): SaveTasksUseCase {
        return SaveTasksUseCase(taskRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSaveVideoUseCase(
        videoRepository: VideoRepository
    ): SaveVideoUseCase {
        return SaveVideoUseCase(videoRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSaveFoodUseCase(
        foodRepository: FoodRepository
    ): SaveFoodUseCase {
        return SaveFoodUseCase(foodRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetStatisticsDataUseCase(
        expenseRepository: ExpenseRepository,
        foodRepository: FoodRepository,
        trackRepository: TrackRepository
    ): GetStatisticsDataUseCase {
        return GetStatisticsDataUseCase(expenseRepository, foodRepository, trackRepository)
    }
}
