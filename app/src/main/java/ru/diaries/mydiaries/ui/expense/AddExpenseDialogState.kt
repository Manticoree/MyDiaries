package ru.diaries.mydiaries.ui.expense

import ru.diaries.mydiaries.data.model.ExpenseCategory

data class AddExpenseDialogState(
    val amount: String = "",
    val selectedCategory: ExpenseCategory = ExpenseCategory.OTHER,
    val description: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)

sealed class AddExpenseDialogIntent {
    data class AmountChanged(val amount: String) : AddExpenseDialogIntent()
    data class CategoryChanged(val category: ExpenseCategory) : AddExpenseDialogIntent()
    data class DescriptionChanged(val description: String) : AddExpenseDialogIntent()
    data object Save : AddExpenseDialogIntent()
    data object ClearError : AddExpenseDialogIntent()
}

sealed class AddExpenseDialogEffect {
    data object SaveSuccess : AddExpenseDialogEffect()
}
