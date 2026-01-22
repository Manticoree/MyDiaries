package ru.diaries.mydiaries.ui.expense

import ru.diaries.mydiaries.data.model.Expense
import ru.diaries.mydiaries.data.model.ExpenseCategory
import java.time.LocalDate

data class ExpenseState(
    val expenses: List<Expense> = emptyList(),
    val todayExpenses: List<Expense> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val editingExpense: Expense? = null,
    val amount: String = "",
    val selectedCategory: ExpenseCategory = ExpenseCategory.OTHER,
    val description: String = "",
    val totalToday: Double = 0.0
)

sealed class ExpenseIntent {
    data object LoadExpenses : ExpenseIntent()
    data class SelectDate(val date: LocalDate) : ExpenseIntent()
    data object ShowAddDialog : ExpenseIntent()
    data object HideAddDialog : ExpenseIntent()
    data class AmountChanged(val amount: String) : ExpenseIntent()
    data class CategoryChanged(val category: ExpenseCategory) : ExpenseIntent()
    data class DescriptionChanged(val description: String) : ExpenseIntent()
    data object SaveExpense : ExpenseIntent()
    data class DeleteExpense(val id: String) : ExpenseIntent()
}
