package ru.diaries.mydiaries.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.diaries.mydiaries.data.local.dao.TaskDao
import ru.diaries.mydiaries.data.local.mapper.toDomainList
import ru.diaries.mydiaries.data.local.mapper.toEntity
import ru.diaries.mydiaries.feature.todo.data.model.Task
import ru.diaries.mydiaries.feature.todo.data.repository.TaskRepository
import javax.inject.Inject

class RoomTaskRepository @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { it.toDomainList() }
    }

    override suspend fun saveTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    override suspend fun toggleTaskCompletion(id: String, isCompleted: Boolean) {
        taskDao.updateTaskCompletion(id, isCompleted)
    }

    override suspend fun deleteTask(id: String) {
        taskDao.deleteTask(id)
    }
}
