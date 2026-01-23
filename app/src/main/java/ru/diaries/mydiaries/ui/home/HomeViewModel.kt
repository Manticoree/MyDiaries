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
import ru.diaries.mydiaries.feature.todo.data.model.Task
import ru.diaries.mydiaries.feature.todo.data.repository.TaskRepository
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DiaryRepository,
    private val expenseRepository: ExpenseRepository,
    private val taskRepository: TaskRepository
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

            is HomeIntent.ShowAddTaskDialog -> showAddTaskDialog()
            is HomeIntent.HideAddTaskDialog -> hideAddTaskDialog()
            is HomeIntent.TaskTitleChanged -> updateTaskTitle(intent.index, intent.title)
            is HomeIntent.AddTaskField -> addTaskField()
            is HomeIntent.RemoveTaskField -> removeTaskField(intent.index)
            is HomeIntent.SaveTasks -> saveTasks()
            is HomeIntent.ToggleTaskCompletion -> toggleTaskCompletion(intent.taskId, intent.isCompleted)
            is HomeIntent.DeleteTask -> deleteTask(intent.id)
        }
    }

    private fun loadEntriesAndExpenses() {
        viewModelScope.launch {
            combine(
                repository.getEntries(),
                expenseRepository.getAllExpenses(),
                taskRepository.getAllTasks()
            ) { entries, expenses, tasks ->
                Triple(entries, expenses, tasks)
            }
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { (entries, expenses, tasks) ->
                    val sortedEntries = entries.sortedByDescending { it.date }
                    val grouped = groupItemsByDate(sortedEntries, expenses, tasks)
                    val todayExpenses = expenses.filter { it.date == LocalDate.now() }
                    val todayTasks = tasks.filter { it.date == LocalDate.now() }
                    _state.update {
                        it.copy(
                            entries = sortedEntries,
                            groupedEntries = grouped,
                            todayExpenses = todayExpenses,
                            todayTasks = todayTasks,
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
        expenses: List<Expense>,
        tasks: List<Task> = emptyList()
    ): List<DateGroup> {
        val allDates = (entries.map { it.date } + expenses.map { it.date } + tasks.map { it.date }).distinct()

        return allDates.map { date ->
            val diaryItems = entries
                .filter { it.date == date }
                .map { TimelineItem.DiaryItem(it) }

            val dayExpenses = expenses.filter { it.date == date }
            val expensesItem = if (dayExpenses.isNotEmpty()) {
                listOf(TimelineItem.ExpensesItem(dayExpenses))
            } else {
                emptyList()
            }

            val dayTasks = tasks.filter { it.date == date }
            val tasksItem = if (dayTasks.isNotEmpty()) {
                listOf(TimelineItem.TasksItem(dayTasks))
            } else {
                emptyList()
            }

            val existingGroup = _state.value.groupedEntries.find { it.date == date }
            DateGroup(
                date = date,
                items = tasksItem + diaryItems + expensesItem,
                isExpanded = existingGroup?.isExpanded ?: true
            )
        }.sortedByDescending { it.date }
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

    private fun showAddTaskDialog() {
        _state.update {
            it.copy(
                showAddTaskDialog = true,
                newTaskTitles = listOf("")
            )
        }
    }

    private fun hideAddTaskDialog() {
        _state.update {
            it.copy(
                showAddTaskDialog = false,
                newTaskTitles = listOf("")
            )
        }
    }

    private fun updateTaskTitle(index: Int, title: String) {
        _state.update { currentState ->
            val updatedTitles = currentState.newTaskTitles.toMutableList()
            if (index in updatedTitles.indices) {
                updatedTitles[index] = title
            }
            currentState.copy(newTaskTitles = updatedTitles)
        }
    }

    private fun addTaskField() {
        _state.update { currentState ->
            currentState.copy(newTaskTitles = currentState.newTaskTitles + "")
        }
    }

    private fun removeTaskField(index: Int) {
        _state.update { currentState ->
            val updatedTitles = currentState.newTaskTitles.toMutableList()
            if (updatedTitles.size > 1 && index in updatedTitles.indices) {
                updatedTitles.removeAt(index)
            }
            currentState.copy(newTaskTitles = updatedTitles)
        }
    }

    private fun saveTasks() {
        val titles = _state.value.newTaskTitles
            .map { it.trim() }
            .filter { it.isNotBlank() }

        if (titles.isEmpty()) return

        viewModelScope.launch {
            titles.forEach { title ->
                val task = Task(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    isCompleted = false,
                    date = LocalDate.now()
                )
                taskRepository.saveTask(task)
            }
            hideAddTaskDialog()
        }
    }

    private fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(taskId, isCompleted)
        }
    }

    private fun deleteTask(id: String) {
        viewModelScope.launch {
            taskRepository.deleteTask(id)
        }
    }
}
