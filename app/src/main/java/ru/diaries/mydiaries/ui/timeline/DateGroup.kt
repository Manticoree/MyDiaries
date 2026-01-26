package ru.diaries.mydiaries.ui.timeline

import java.time.LocalDate

data class DateGroup(
    val date: LocalDate,
    val items: List<TimelineItem>,
    val isExpanded: Boolean = true
) {
    val entryCount: Int get() = items.count { it is TimelineItem.DiaryItem }
    val expenseCount: Int get() = items.filterIsInstance<TimelineItem.ExpensesItem>()
        .sumOf { it.expenses.size }
    val taskCount: Int get() = items.filterIsInstance<TimelineItem.TasksItem>()
        .sumOf { it.tasks.size }
    val totalCount: Int get() = items.size
}
