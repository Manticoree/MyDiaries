package ru.diaries.mydiaries.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.data.model.DiaryEntry
import ru.diaries.mydiaries.data.model.Expense
import ru.diaries.mydiaries.data.model.ExpenseCategory
import ru.diaries.mydiaries.data.repository.DiaryRepository
import ru.diaries.mydiaries.data.repository.ExpenseRepository
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DiaryRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        handleIntent(HomeIntent.LoadEntries)
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadEntries -> loadEntriesAndExpenses()
            is HomeIntent.DeleteEntry -> deleteEntry(intent.entryId)
            is HomeIntent.EntryClicked -> openEditor(intent.entryId)
            is HomeIntent.AddEntryClicked -> openEditor(null)
            is HomeIntent.ToggleDateGroup -> toggleDateGroup(intent.date)
            is HomeIntent.CloseEditorDialog -> closeEditor()

            is HomeIntent.ShowActionChoiceDialog -> showActionChoiceDialog()
            is HomeIntent.HideActionChoiceDialog -> hideActionChoiceDialog()

            is HomeIntent.LoadTodayExpenses -> { /* Handled in combined flow */ }
            is HomeIntent.ShowAddExpenseDialog -> showAddExpenseDialog()
            is HomeIntent.HideAddExpenseDialog -> hideAddExpenseDialog()
            is HomeIntent.ShowExpenseStatsDialog -> showExpenseStatsDialog()
            is HomeIntent.HideExpenseStatsDialog -> hideExpenseStatsDialog()
            is HomeIntent.ExpenseAmountChanged -> updateExpenseAmount(intent.amount)
            is HomeIntent.ExpenseCategoryChanged -> updateExpenseCategory(intent.category)
            is HomeIntent.ExpenseDescriptionChanged -> updateExpenseDescription(intent.description)
            is HomeIntent.SaveExpense -> saveExpense()
            is HomeIntent.DeleteExpense -> deleteExpense(intent.id)
        }
    }

    private fun loadEntriesAndExpenses() {
        viewModelScope.launch {
            combine(
                repository.getEntries(),
                expenseRepository.getAllExpenses()
            ) { entries, expenses ->
                Pair(entries, expenses)
            }
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { (entries, expenses) ->
                    val sortedEntries = entries.sortedByDescending { it.date }
                    val grouped = groupItemsByDate(sortedEntries, expenses)
                    val todayExpenses = expenses.filter { it.date == LocalDate.now() }
                    _state.update {
                        it.copy(
                            entries = sortedEntries,
                            groupedEntries = grouped,
                            todayExpenses = todayExpenses,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun deleteEntry(entryId: String) {
        viewModelScope.launch {
            repository.deleteEntry(entryId)
        }
    }

    private fun openEditor(entryId: String?) {
        _state.update {
            it.copy(
                showEditorDialog = true,
                editingEntryId = entryId,
                editorDialogKey = if (entryId == null) {
                    "new_${System.currentTimeMillis()}"
                } else {
                    "edit_$entryId"
                }
            )
        }
    }

    private fun closeEditor() {
        _state.update {
            it.copy(
                showEditorDialog = false,
                editingEntryId = null,
                editorDialogKey = ""
            )
        }
    }

    private fun groupItemsByDate(
        entries: List<DiaryEntry>,
        expenses: List<Expense>
    ): List<DateGroup> {
        val diaryItems = entries.map { TimelineItem.DiaryItem(it) }
        val expenseItems = expenses.map { TimelineItem.ExpenseItem(it) }
        val allItems = diaryItems + expenseItems

        return allItems
            .groupBy { it.date }
            .map { (date, itemsForDate) ->
                val existingGroup = _state.value.groupedEntries.find { it.date == date }
                DateGroup(
                    date = date,
                    items = itemsForDate.sortedWith(
                        compareBy<TimelineItem> { item ->
                            when (item) {
                                is TimelineItem.DiaryItem -> 0
                                is TimelineItem.ExpenseItem -> 1
                            }
                        }
                    ),
                    isExpanded = existingGroup?.isExpanded ?: true
                )
            }
            .sortedByDescending { it.date }
    }

    private fun toggleDateGroup(date: LocalDate) {
        _state.update { currentState ->
            currentState.copy(
                groupedEntries = currentState.groupedEntries.map { group ->
                    if (group.date == date) {
                        group.copy(isExpanded = !group.isExpanded)
                    } else {
                        group
                    }
                }
            )
        }
    }

    private fun showActionChoiceDialog() {
        _state.update { it.copy(showActionChoiceDialog = true) }
    }

    private fun hideActionChoiceDialog() {
        _state.update { it.copy(showActionChoiceDialog = false) }
    }

    private fun showAddExpenseDialog() {
        _state.update {
            it.copy(
                showAddExpenseDialog = true,
                expenseAmount = "",
                selectedExpenseCategory = ExpenseCategory.OTHER,
                expenseDescription = ""
            )
        }
    }

    private fun hideAddExpenseDialog() {
        _state.update {
            it.copy(
                showAddExpenseDialog = false,
                expenseAmount = "",
                selectedExpenseCategory = ExpenseCategory.OTHER,
                expenseDescription = ""
            )
        }
    }

    private fun showExpenseStatsDialog() {
        _state.update { it.copy(showExpenseStatsDialog = true) }
    }

    private fun hideExpenseStatsDialog() {
        _state.update { it.copy(showExpenseStatsDialog = false) }
    }

    private fun updateExpenseAmount(amount: String) {
        val filtered = amount.filter { it.isDigit() || it == '.' || it == ',' }
            .replace(',', '.')
        _state.update { it.copy(expenseAmount = filtered) }
    }

    private fun updateExpenseCategory(category: ExpenseCategory) {
        _state.update { it.copy(selectedExpenseCategory = category) }
    }

    private fun updateExpenseDescription(description: String) {
        _state.update { it.copy(expenseDescription = description) }
    }

    private fun saveExpense() {
        val currentState = _state.value
        val amount = currentState.expenseAmount.toDoubleOrNull() ?: return

        if (amount <= 0) return

        viewModelScope.launch {
            val expense = Expense(
                amount = amount,
                category = currentState.selectedExpenseCategory,
                description = currentState.expenseDescription.trim(),
                date = LocalDate.now()
            )
            expenseRepository.saveExpense(expense)
            hideAddExpenseDialog()
        }
    }

    private fun deleteExpense(id: String) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(id)
        }
    }
}
