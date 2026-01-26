package ru.diaries.mydiaries.domain.usecase.task

import ru.diaries.mydiaries.feature.todo.data.model.Task
import ru.diaries.mydiaries.feature.todo.data.repository.TaskRepository
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

class SaveTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(
        titles: List<String>,
        date: LocalDate = LocalDate.now()
    ): Result<Int> {
        val validTitles = titles
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (validTitles.isEmpty()) {
            return Result.failure(IllegalArgumentException("At least one valid task title is required"))
        }

        return try {
            validTitles.forEach { title ->
                val task = Task(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    isCompleted = false,
                    date = date
                )
                taskRepository.saveTask(task)
            }
            Result.success(validTitles.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
