package ru.diaries.mydiaries.feature.todo.data.repository

import kotlinx.coroutines.flow.Flow
import ru.diaries.mydiaries.feature.todo.data.model.Task

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun saveTask(task: Task)
    suspend fun toggleTaskCompletion(id: String, isCompleted: Boolean)
    suspend fun deleteTask(id: String)
}
