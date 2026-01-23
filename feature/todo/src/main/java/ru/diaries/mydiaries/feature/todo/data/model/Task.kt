package ru.diaries.mydiaries.feature.todo.data.model

import java.time.LocalDate

data class Task(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val date: LocalDate = LocalDate.now(),
    val createdAt: Long = System.currentTimeMillis()
)
