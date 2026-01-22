package ru.diaries.mydiaries.ui.home

import ru.diaries.mydiaries.data.model.DiaryEntry
import ru.diaries.mydiaries.data.model.Expense

data class HomeState(
    val entries: List<DiaryEntry> = emptyList(),
    val groupedEntries: List<DateGroup> = emptyList(),
    val todayExpenses: List<Expense> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val showEditorDialog: Boolean = false,
    val editingEntryId: String? = null,
    val editorDialogKey: String = "",
    val showActionChoiceDialog: Boolean = false,
    val showAddExpenseDialog: Boolean = false,
    val showExpenseStatsDialog: Boolean = false,
    val expenseAmount: String = "",
    val selectedExpenseCategory: ru.diaries.mydiaries.data.model.ExpenseCategory = ru.diaries.mydiaries.data.model.ExpenseCategory.OTHER,
    val expenseDescription: String = ""
)
