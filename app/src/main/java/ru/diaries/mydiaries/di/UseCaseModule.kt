package ru.diaries.mydiaries.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import ru.diaries.mydiaries.data.repository.DiaryRepository
import ru.diaries.mydiaries.data.repository.ExpenseRepository
import ru.diaries.mydiaries.domain.usecase.expense.SaveExpenseUseCase
import ru.diaries.mydiaries.domain.usecase.food.SaveFoodUseCase
import ru.diaries.mydiaries.domain.usecase.task.SaveTasksUseCase
import ru.diaries.mydiaries.domain.usecase.timeline.GetTimelineItemsUseCase
import ru.diaries.mydiaries.domain.usecase.video.SaveVideoUseCase
import ru.diaries.mydiaries.feature.food.data.repository.FoodRepository
import ru.diaries.mydiaries.feature.todo.data.repository.TaskRepository
import ru.diaries.mydiaries.feature.video.data.repository.VideoRepository

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
        foodRepository: FoodRepository
    ): GetTimelineItemsUseCase {
        return GetTimelineItemsUseCase(diaryRepository, expenseRepository, taskRepository, videoRepository, foodRepository)
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
}
