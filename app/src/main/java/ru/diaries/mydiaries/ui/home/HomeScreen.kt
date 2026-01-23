package ru.diaries.mydiaries.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import ru.diaries.mydiaries.R
import ru.diaries.mydiaries.ui.components.ActionChoiceDialog
import ru.diaries.mydiaries.ui.components.DateGroupHeader
import ru.diaries.mydiaries.ui.components.DayExpensesCard
import ru.diaries.mydiaries.ui.components.DiaryEntryCard
import ru.diaries.mydiaries.ui.components.EmptyStateView
import ru.diaries.mydiaries.ui.components.MiniPieChart
import ru.diaries.mydiaries.ui.editor.EditorScreen
import ru.diaries.mydiaries.ui.expense.AddExpenseDialog
import ru.diaries.mydiaries.ui.expense.ExpenseStatsDialog
import ru.diaries.mydiaries.feature.todo.ui.AddTaskDialog
import ru.diaries.mydiaries.feature.todo.ui.DayTasksCard
import ru.diaries.mydiaries.ui.theme.GoldenHoney
import ru.diaries.mydiaries.ui.theme.GreetingStyle
import ru.diaries.mydiaries.ui.theme.NumberLargeStyle
import ru.diaries.mydiaries.ui.theme.Terracotta
import ru.diaries.mydiaries.ui.theme.WarmBrown
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.home_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(HomeIntent.ShowActionChoiceDialog) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        spotColor = WarmBrown.copy(alpha = 0.3f)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_entry)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item(key = "expense_card") {
                            TodayExpenseCard(
                                expenses = state.todayExpenses,
                                onClick = { viewModel.handleIntent(HomeIntent.ShowExpenseStatsDialog) }
                            )
                        }

                        if (state.groupedEntries.isEmpty()) {
                            item(key = "empty_state") {
                                EmptyStateView(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 64.dp)
                                )
                            }
                        } else {
                            state.groupedEntries.forEach { dateGroup ->
                                item(key = "header_${dateGroup.date}") {
                                    DateGroupHeader(
                                        date = dateGroup.date,
                                        entryCount = dateGroup.entryCount,
                                        expenseCount = dateGroup.expenseCount,
                                        isExpanded = dateGroup.isExpanded,
                                        onClick = { viewModel.handleIntent(HomeIntent.ToggleDateGroup(dateGroup.date)) },
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                    )
                                }

                                item(key = "items_${dateGroup.date}") {
                                    AnimatedItemsColumn(
                                        items = dateGroup.items,
                                        isExpanded = dateGroup.isExpanded,
                                        onEntryClick = { entryId ->
                                            viewModel.handleIntent(HomeIntent.EntryClicked(entryId))
                                        },
                                        onEntryDelete = { entryId ->
                                            viewModel.handleIntent(HomeIntent.DeleteEntry(entryId))
                                        },
                                        onExpenseDelete = { expenseId ->
                                            viewModel.handleIntent(HomeIntent.DeleteExpense(expenseId))
                                        },
                                        onToggleTask = { taskId, isCompleted ->
                                            viewModel.handleIntent(HomeIntent.ToggleTaskCompletion(taskId, isCompleted))
                                        },
                                        onDeleteTask = { taskId ->
                                            viewModel.handleIntent(HomeIntent.DeleteTask(taskId))
                                        }
                                    )
                                }

                                item(key = "spacer_${dateGroup.date}") {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Editor Dialog
    if (state.showEditorDialog) {
        key(state.editorDialogKey) {
            Dialog(
                onDismissRequest = { viewModel.handleIntent(HomeIntent.CloseEditorDialog) },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                EditorScreen(
                    entryId = state.editingEntryId,
                    onNavigateBack = { viewModel.handleIntent(HomeIntent.CloseEditorDialog) }
                )
            }
        }
    }

    // Action Choice Dialog
    if (state.showActionChoiceDialog) {
        ActionChoiceDialog(
            onAddEntry = { viewModel.handleIntent(HomeIntent.AddEntryClicked) },
            onAddExpense = { viewModel.handleIntent(HomeIntent.ShowAddExpenseDialog) },
            onAddTask = { viewModel.handleIntent(HomeIntent.ShowAddTaskDialog) },
            onDismiss = { viewModel.handleIntent(HomeIntent.HideActionChoiceDialog) }
        )
    }

    // Add Expense Dialog
    if (state.showAddExpenseDialog) {
        AddExpenseDialog(
            amount = state.expenseAmount,
            selectedCategory = state.selectedExpenseCategory,
            description = state.expenseDescription,
            onAmountChange = { viewModel.handleIntent(HomeIntent.ExpenseAmountChanged(it)) },
            onCategoryChange = { viewModel.handleIntent(HomeIntent.ExpenseCategoryChanged(it)) },
            onDescriptionChange = { viewModel.handleIntent(HomeIntent.ExpenseDescriptionChanged(it)) },
            onSave = { viewModel.handleIntent(HomeIntent.SaveExpense) },
            onDismiss = { viewModel.handleIntent(HomeIntent.HideAddExpenseDialog) }
        )
    }

    // Expense Stats Dialog
    if (state.showExpenseStatsDialog) {
        ExpenseStatsDialog(
            expenses = state.todayExpenses,
            date = LocalDate.now(),
            onDelete = { viewModel.handleIntent(HomeIntent.DeleteExpense(it)) },
            onDismiss = { viewModel.handleIntent(HomeIntent.HideExpenseStatsDialog) }
        )
    }

    // Add Task Dialog
    if (state.showAddTaskDialog) {
        AddTaskDialog(
            taskTitles = state.newTaskTitles,
            onTaskTitleChange = { index, title ->
                viewModel.handleIntent(HomeIntent.TaskTitleChanged(index, title))
            },
            onAddTaskField = { viewModel.handleIntent(HomeIntent.AddTaskField) },
            onRemoveTaskField = { index -> viewModel.handleIntent(HomeIntent.RemoveTaskField(index)) },
            onSave = { viewModel.handleIntent(HomeIntent.SaveTasks) },
            onDismiss = { viewModel.handleIntent(HomeIntent.HideAddTaskDialog) },
            dialogTitle = stringResource(R.string.add_tasks),
            titlePlaceholder = stringResource(R.string.task_title_placeholder),
            saveText = stringResource(R.string.add),
            cancelText = stringResource(R.string.cancel),
            addMoreText = stringResource(R.string.add_more_task)
        )
    }
}

@Composable
private fun TodayExpenseCard(
    expenses: List<ru.diaries.mydiaries.data.model.Expense>,
    onClick: () -> Unit
) {
    val total = expenses.sumOf { it.amount }
    val greeting = getGreetingByTimeOfDay()

    // Gradient for top of card
    val headerGradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = WarmBrown.copy(alpha = 0.15f),
                ambientColor = WarmBrown.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        onClick = onClick
    ) {
        Column {
            // Gradient header with greeting
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerGradient)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = greeting,
                    style = GreetingStyle,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Main content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pie chart
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            spotColor = WarmBrown.copy(alpha = 0.1f)
                        )
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    MiniPieChart(
                        expenses = expenses,
                        size = 48.dp
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Amount and label
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.today_expenses),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatCurrency(total),
                        style = NumberLargeStyle,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // "View details" button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.view_details),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getGreetingByTimeOfDay(): String {
    val currentHour = LocalTime.now().hour
    return when (currentHour) {
        in 5..11 -> stringResource(R.string.greeting_morning)
        in 12..17 -> stringResource(R.string.greeting_afternoon)
        in 18..22 -> stringResource(R.string.greeting_evening)
        else -> stringResource(R.string.greeting_night)
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    return format.format(amount)
}

@Composable
private fun AnimatedItemsColumn(
    items: List<TimelineItem>,
    isExpanded: Boolean,
    onEntryClick: (String) -> Unit,
    onEntryDelete: (String) -> Unit,
    onExpenseDelete: (String) -> Unit,
    onToggleTask: (String, Boolean) -> Unit = { _, _ -> },
    onDeleteTask: (String) -> Unit = {}
) {
    AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            ),
            expandFrom = Alignment.Top
        ) + fadeIn(
            animationSpec = tween(durationMillis = 300)
        ),
        exit = shrinkVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            shrinkTowards = Alignment.Top
        ) + fadeOut(
            animationSpec = tween(durationMillis = 200)
        )
    ) {
        Column {
            items.forEachIndexed { index, item ->
                key(item.id) {
                    AnimatedTimelineItem(
                        item = item,
                        index = index,
                        onEntryClick = onEntryClick,
                        onEntryDelete = onEntryDelete,
                        onExpenseDelete = onExpenseDelete,
                        onToggleTask = onToggleTask,
                        onDeleteTask = onDeleteTask
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedTimelineItem(
    item: TimelineItem,
    index: Int,
    onEntryClick: (String) -> Unit,
    onEntryDelete: (String) -> Unit,
    onExpenseDelete: (String) -> Unit,
    onToggleTask: (String, Boolean) -> Unit = { _, _ -> },
    onDeleteTask: (String) -> Unit = {}
) {
    val staggerDelay = index * 50

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            animationSpec = tween(
                durationMillis = 400,
                delayMillis = staggerDelay
            ),
            initialOffsetY = { -40 }
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 350,
                delayMillis = staggerDelay
            )
        ),
        exit = slideOutVertically(
            animationSpec = tween(durationMillis = 200),
            targetOffsetY = { -20 }
        ) + fadeOut(
            animationSpec = tween(durationMillis = 150)
        )
    ) {
        when (item) {
            is TimelineItem.DiaryItem -> {
                DiaryEntryCard(
                    entry = item.entry,
                    onClick = { onEntryClick(item.entry.id) },
                    onDelete = { onEntryDelete(item.entry.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            is TimelineItem.ExpensesItem -> {
                DayExpensesCard(
                    expenses = item.expenses,
                    onDeleteExpense = onExpenseDelete,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            is TimelineItem.TasksItem -> {
                DayTasksCard(
                    tasks = item.tasks,
                    onToggleTask = onToggleTask,
                    onDeleteTask = onDeleteTask,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    cardTitle = stringResource(R.string.tasks_for_day),
                    completedText = stringResource(R.string.task_completed_short)
                )
            }
        }
    }
}
