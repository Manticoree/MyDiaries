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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import ru.diaries.mydiaries.R
import ru.diaries.mydiaries.ui.components.ActionChoiceDialog
import ru.diaries.mydiaries.ui.components.DateGroupHeader
import ru.diaries.mydiaries.ui.components.DiaryEntryCard
import ru.diaries.mydiaries.ui.components.EmptyStateView
import ru.diaries.mydiaries.ui.components.ExpenseCard
import ru.diaries.mydiaries.ui.components.MiniPieChart
import ru.diaries.mydiaries.ui.editor.EditorScreen
import ru.diaries.mydiaries.ui.expense.AddExpenseDialog
import ru.diaries.mydiaries.ui.expense.ExpenseStatsDialog
import java.text.NumberFormat
import java.time.LocalDate
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
                title = { Text(stringResource(R.string.home_title)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(HomeIntent.ShowActionChoiceDialog) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_entry)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
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
                                        onClick = { viewModel.handleIntent(HomeIntent.ToggleDateGroup(dateGroup.date)) }
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
                                        }
                                    )
                                }

                                item(key = "spacer_${dateGroup.date}") {
                                    Spacer(modifier = Modifier.height(16.dp))
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
}

@Composable
private fun TodayExpenseCard(
    expenses: List<ru.diaries.mydiaries.data.model.Expense>,
    onClick: () -> Unit
) {
    val total = expenses.sumOf { it.amount }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MiniPieChart(
                expenses = expenses,
                size = 48.dp,
                strokeWidth = 8.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.today_expenses),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatCurrency(total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = stringResource(R.string.view_expenses),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
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
    onExpenseDelete: (String) -> Unit
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
                        onExpenseDelete = onExpenseDelete
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
    onExpenseDelete: (String) -> Unit
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
            is TimelineItem.ExpenseItem -> {
                ExpenseCard(
                    expense = item.expense,
                    onDelete = { onExpenseDelete(item.expense.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}
