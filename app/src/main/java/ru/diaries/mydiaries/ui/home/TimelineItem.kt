package ru.diaries.mydiaries.ui.home

import ru.diaries.mydiaries.data.model.DiaryEntry
import ru.diaries.mydiaries.data.model.Expense
import ru.diaries.mydiaries.feature.todo.data.model.Task
import java.time.LocalDate

sealed class TimelineItem {
    abstract val id: String
    abstract val date: LocalDate

    data class DiaryItem(val entry: DiaryEntry) : TimelineItem() {
        override val id: String get() = "diary_${entry.id}"
        override val date: LocalDate get() = entry.date
    }

    data class ExpensesItem(val expenses: List<Expense>) : TimelineItem() {
        override val id: String get() = "expenses_${expenses.firstOrNull()?.date ?: LocalDate.now()}"
        override val date: LocalDate get() = expenses.firstOrNull()?.date ?: LocalDate.now()
        val total: Double get() = expenses.sumOf { it.amount }
    }

    data class TasksItem(val tasks: List<Task>) : TimelineItem() {
        override val id: String get() = "tasks_${tasks.firstOrNull()?.date ?: LocalDate.now()}"
        override val date: LocalDate get() = tasks.firstOrNull()?.date ?: LocalDate.now()
        val completedCount: Int get() = tasks.count { it.isCompleted }
        val totalCount: Int get() = tasks.size
    }
}
