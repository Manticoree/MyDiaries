package ru.diaries.mydiaries.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.diaries.mydiaries.data.repository.DiaryRepository
import ru.diaries.mydiaries.data.repository.ExpenseRepository
import ru.diaries.mydiaries.data.repository.RoomDiaryRepository
import ru.diaries.mydiaries.data.repository.RoomExpenseRepository
import ru.diaries.mydiaries.data.repository.RoomFoodRepository
import ru.diaries.mydiaries.data.repository.RoomTaskRepository
import ru.diaries.mydiaries.data.repository.RoomTrackRepository
import ru.diaries.mydiaries.data.repository.RoomVideoRepository
import ru.diaries.mydiaries.feature.food.data.repository.FoodRepository
import ru.diaries.mydiaries.feature.todo.data.repository.TaskRepository
import ru.diaries.mydiaries.feature.track.data.repository.TrackRepository
import ru.diaries.mydiaries.feature.video.data.repository.VideoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDiaryRepository(
        roomDiaryRepository: RoomDiaryRepository
    ): DiaryRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        roomExpenseRepository: RoomExpenseRepository
    ): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        roomTaskRepository: RoomTaskRepository
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindVideoRepository(
        roomVideoRepository: RoomVideoRepository
    ): VideoRepository

    @Binds
    @Singleton
    abstract fun bindFoodRepository(
        roomFoodRepository: RoomFoodRepository
    ): FoodRepository

    @Binds
    @Singleton
    abstract fun bindTrackRepository(
        roomTrackRepository: RoomTrackRepository
    ): TrackRepository
}
