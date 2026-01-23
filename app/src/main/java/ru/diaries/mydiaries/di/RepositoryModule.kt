package ru.diaries.mydiaries.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.diaries.mydiaries.data.repository.DiaryRepository
import ru.diaries.mydiaries.data.repository.ExpenseRepository
import ru.diaries.mydiaries.data.repository.RoomDiaryRepository
import ru.diaries.mydiaries.data.repository.RoomExpenseRepository
import ru.diaries.mydiaries.data.repository.RoomTaskRepository
import ru.diaries.mydiaries.feature.todo.data.repository.TaskRepository
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
}
