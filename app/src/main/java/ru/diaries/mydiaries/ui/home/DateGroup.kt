package ru.diaries.mydiaries.ui.home

import java.time.LocalDate

data class DateGroup(
    val date: LocalDate,
    val items: List<TimelineItem>,
    val isExpanded: Boolean = true
) {
    val entryCount: Int get() = items.count { it is TimelineItem.DiaryItem }
    val expenseCount: Int get() = items.count { it is TimelineItem.ExpenseItem }
    val totalCount: Int get() = items.size
}
