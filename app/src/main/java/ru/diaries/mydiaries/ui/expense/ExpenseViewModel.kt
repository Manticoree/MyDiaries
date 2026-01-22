package ru.diaries.mydiaries.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.data.model.Expense
import ru.diaries.mydiaries.data.model.ExpenseCategory
import ru.diaries.mydiaries.data.repository.ExpenseRepository
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseState())
    val state: StateFlow<ExpenseState> = _state.asStateFlow()

    init {
        handleIntent(ExpenseIntent.LoadExpenses)
    }

    fun handleIntent(intent: ExpenseIntent) {
        when (intent) {
            is ExpenseIntent.LoadExpenses -> loadExpenses()
            is ExpenseIntent.SelectDate -> selectDate(intent.date)
            is ExpenseIntent.ShowAddDialog -> showAddDialog()
            is ExpenseIntent.HideAddDialog -> hideAddDialog()
            is ExpenseIntent.AmountChanged -> updateAmount(intent.amount)
            is ExpenseIntent.CategoryChanged -> updateCategory(intent.category)
            is ExpenseIntent.DescriptionChanged -> updateDescription(intent.description)
            is ExpenseIntent.SaveExpense -> saveExpense()
            is ExpenseIntent.DeleteExpense -> deleteExpense(intent.id)
        }
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            repository.getExpensesByDate(_state.value.selectedDate)
                .collect { expenses ->
                    val total = expenses.sumOf { it.amount }
                    _state.update {
                        it.copy(
                            todayExpenses = expenses,
                            totalToday = total,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun selectDate(date: LocalDate) {
        _state.update { it.copy(selectedDate = date, isLoading = true) }
        loadExpenses()
    }

    private fun showAddDialog() {
        _state.update {
            it.copy(
                showAddDialog = true,
                amount = "",
                selectedCategory = ExpenseCategory.OTHER,
                description = ""
            )
        }
    }

    private fun hideAddDialog() {
        _state.update {
            it.copy(
                showAddDialog = false,
                amount = "",
                selectedCategory = ExpenseCategory.OTHER,
                description = ""
            )
        }
    }

    private fun updateAmount(amount: String) {
        val filtered = amount.filter { it.isDigit() || it == '.' || it == ',' }
            .replace(',', '.')
        _state.update { it.copy(amount = filtered) }
    }

    private fun updateCategory(category: ExpenseCategory) {
        _state.update { it.copy(selectedCategory = category) }
    }

    private fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    private fun saveExpense() {
        val currentState = _state.value
        val amount = currentState.amount.toDoubleOrNull() ?: return

        if (amount <= 0) return

        viewModelScope.launch {
            val expense = Expense(
                amount = amount,
                category = currentState.selectedCategory,
                description = currentState.description.trim(),
                date = currentState.selectedDate
            )
            repository.saveExpense(expense)
            hideAddDialog()
        }
    }

    private fun deleteExpense(id: String) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }
}
