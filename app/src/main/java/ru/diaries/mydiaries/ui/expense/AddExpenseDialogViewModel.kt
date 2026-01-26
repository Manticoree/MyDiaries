package ru.diaries.mydiaries.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.data.model.ExpenseCategory
import ru.diaries.mydiaries.domain.usecase.expense.SaveExpenseUseCase
import javax.inject.Inject

@HiltViewModel
class AddExpenseDialogViewModel @Inject constructor(
    private val saveExpenseUseCase: SaveExpenseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddExpenseDialogState())
    val state: StateFlow<AddExpenseDialogState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AddExpenseDialogEffect>()
    val effect: SharedFlow<AddExpenseDialogEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: AddExpenseDialogIntent) {
        when (intent) {
            is AddExpenseDialogIntent.AmountChanged -> updateAmount(intent.amount)
            is AddExpenseDialogIntent.CategoryChanged -> updateCategory(intent.category)
            is AddExpenseDialogIntent.DescriptionChanged -> updateDescription(intent.description)
            is AddExpenseDialogIntent.Save -> saveExpense()
            is AddExpenseDialogIntent.ClearError -> clearError()
        }
    }

    private fun updateAmount(amount: String) {
        val filtered = amount.filter { it.isDigit() || it == '.' || it == ',' }
            .replace(',', '.')
        _state.update { it.copy(amount = filtered, error = null) }
    }

    private fun updateCategory(category: ExpenseCategory) {
        _state.update { it.copy(selectedCategory = category) }
    }

    private fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    private fun saveExpense() {
        val currentState = _state.value
        val amount = currentState.amount.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            _state.update { it.copy(error = "Please enter a valid amount") }
            return
        }

        _state.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            saveExpenseUseCase(
                amount = amount,
                category = currentState.selectedCategory,
                description = currentState.description
            ).fold(
                onSuccess = {
                    _state.update { it.copy(isSaving = false) }
                    _effect.emit(AddExpenseDialogEffect.SaveSuccess)
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            error = e.message ?: "Failed to save expense"
                        )
                    }
                }
            )
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
