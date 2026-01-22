package ru.diaries.mydiaries.ui.home

import ru.diaries.mydiaries.data.model.DiaryEntry
import ru.diaries.mydiaries.data.model.Expense
import java.time.LocalDate

sealed class TimelineItem {
    abstract val id: String
    abstract val date: LocalDate

    data class DiaryItem(val entry: DiaryEntry) : TimelineItem() {
        override val id: String get() = "diary_${entry.id}"
        override val date: LocalDate get() = entry.date
    }

    data class ExpenseItem(val expense: Expense) : TimelineItem() {
        override val id: String get() = "expense_${expense.id}"
        override val date: LocalDate get() = expense.date
    }
}
