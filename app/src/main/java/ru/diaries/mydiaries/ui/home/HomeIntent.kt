package ru.diaries.mydiaries.ui.home

import ru.diaries.mydiaries.data.model.ExpenseCategory
import java.time.LocalDate

sealed class HomeIntent {
    data object LoadEntries : HomeIntent()
    data class DeleteEntry(val entryId: String) : HomeIntent()
    data class EntryClicked(val entryId: String) : HomeIntent()
    data object AddEntryClicked : HomeIntent()
    data class ToggleDateGroup(val date: LocalDate) : HomeIntent()
    data object CloseEditorDialog : HomeIntent()

    // Action Choice Dialog
    data object ShowActionChoiceDialog : HomeIntent()
    data object HideActionChoiceDialog : HomeIntent()

    // Expense
    data object LoadTodayExpenses : HomeIntent()
    data object ShowAddExpenseDialog : HomeIntent()
    data object HideAddExpenseDialog : HomeIntent()
    data object ShowExpenseStatsDialog : HomeIntent()
    data object HideExpenseStatsDialog : HomeIntent()
    data class ExpenseAmountChanged(val amount: String) : HomeIntent()
    data class ExpenseCategoryChanged(val category: ExpenseCategory) : HomeIntent()
    data class ExpenseDescriptionChanged(val description: String) : HomeIntent()
    data object SaveExpense : HomeIntent()
    data class DeleteExpense(val id: String) : HomeIntent()

    // Task
    data object ShowAddTaskDialog : HomeIntent()
    data object HideAddTaskDialog : HomeIntent()
    data class TaskTitleChanged(val index: Int, val title: String) : HomeIntent()
    data object AddTaskField : HomeIntent()
    data class RemoveTaskField(val index: Int) : HomeIntent()
    data object SaveTasks : HomeIntent()
    data class ToggleTaskCompletion(val taskId: String, val isCompleted: Boolean) : HomeIntent()
    data class DeleteTask(val id: String) : HomeIntent()
}
