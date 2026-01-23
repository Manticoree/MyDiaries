package ru.diaries.mydiaries.data.local.mapper

import ru.diaries.mydiaries.data.local.entity.TaskEntity
import ru.diaries.mydiaries.feature.todo.data.model.Task
import java.time.LocalDate

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        isCompleted = isCompleted,
        date = LocalDate.parse(date),
        createdAt = createdAt
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        isCompleted = isCompleted,
        date = date.toString(),
        createdAt = createdAt
    )
}

fun List<TaskEntity>.toDomainList(): List<Task> = map { it.toDomain() }
