package ru.diaries.mydiaries.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.diaries.mydiaries.R
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import ru.diaries.mydiaries.ui.statistics.charts.CaloriesChartScreen
import ru.diaries.mydiaries.ui.statistics.charts.DistanceChartScreen
import ru.diaries.mydiaries.ui.statistics.charts.ExpenseChartScreen
import ru.diaries.mydiaries.ui.statistics.charts.StepsChartScreen
import ru.diaries.mydiaries.ui.theme.GradientColors
import ru.diaries.mydiaries.ui.theme.InkBlue
import ru.diaries.mydiaries.ui.theme.LavenderMist
import ru.diaries.mydiaries.ui.theme.SageGreen
import ru.diaries.mydiaries.ui.theme.GoldenHoney
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.statistics_title),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            val data = state.statisticsData

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // Streak Info Card
                item {
                    StreakCard(
                        currentStreak = state.currentStreak,
                        longestStreak = state.longestStreak,
                        onAchievementsClick = {
                            viewModel.handleIntent(StatisticsIntent.ShowAchievements)
                        }
                    )
                }

                item {
                    val numberFormat = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
                    StatisticsCardItem(
                        emoji = "\uD83D\uDCB0",
                        title = stringResource(R.string.statistics_expenses_month),
                        subtitle = numberFormat.format(data?.totalExpenses ?: 0.0),
                        gradientColors = GradientColors.WarmSunrise,
                        onClick = { viewModel.handleIntent(StatisticsIntent.OpenChart(StatisticsChartType.EXPENSES)) }
                    )
                }

                item {
                    val numFormat = NumberFormat.getNumberInstance(Locale("ru", "RU"))
                    StatisticsCardItem(
                        emoji = "\uD83D\uDEB6",
                        title = stringResource(R.string.statistics_steps_month),
                        subtitle = "${numFormat.format(data?.totalSteps ?: 0)} ${stringResource(R.string.unit_steps)}",
                        gradientColors = listOf(SageGreen, GoldenHoney),
                        onClick = { viewModel.handleIntent(StatisticsIntent.OpenChart(StatisticsChartType.STEPS)) }
                    )
                }

                item {
                    val numFormat = NumberFormat.getNumberInstance(Locale("ru", "RU"))
                    StatisticsCardItem(
                        emoji = "\uD83C\uDF54",
                        title = stringResource(R.string.statistics_calories_month),
                        subtitle = "${numFormat.format(data?.totalCalories ?: 0)} ${stringResource(R.string.unit_kcal)}",
                        gradientColors = listOf(GoldenHoney, LavenderMist),
                        onClick = { viewModel.handleIntent(StatisticsIntent.OpenChart(StatisticsChartType.CALORIES)) }
                    )
                }

                item {
                    val numFormat = NumberFormat.getNumberInstance(Locale("ru", "RU")).apply {
                        maximumFractionDigits = 1
                    }
                    StatisticsCardItem(
                        emoji = "\uD83C\uDFC3",
                        title = stringResource(R.string.statistics_activity_month),
                        subtitle = "${numFormat.format(data?.totalDistanceKm ?: 0.0)} ${stringResource(R.string.unit_km)}",
                        gradientColors = listOf(InkBlue, SageGreen),
                        onClick = { viewModel.handleIntent(StatisticsIntent.OpenChart(StatisticsChartType.DISTANCE)) }
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }

    state.openChart?.let { chartType ->
        Dialog(
            onDismissRequest = { viewModel.handleIntent(StatisticsIntent.CloseChart) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val data = state.statisticsData ?: return@Dialog
            when (chartType) {
                StatisticsChartType.EXPENSES -> ExpenseChartScreen(
                    expenses = data.expenses,
                    dailyExpenses = data.dailyExpenses,
                    totalExpenses = data.totalExpenses,
                    onBack = { viewModel.handleIntent(StatisticsIntent.CloseChart) }
                )
                StatisticsChartType.STEPS -> StepsChartScreen(
                    dailySteps = data.dailySteps,
                    totalSteps = data.totalSteps,
                    onBack = { viewModel.handleIntent(StatisticsIntent.CloseChart) }
                )
                StatisticsChartType.CALORIES -> CaloriesChartScreen(
                    dailyCalories = data.dailyCalories,
                    totalCalories = data.totalCalories,
                    onBack = { viewModel.handleIntent(StatisticsIntent.CloseChart) }
                )
                StatisticsChartType.DISTANCE -> DistanceChartScreen(
                    dailyDistance = data.dailyDistance,
                    totalDistanceKm = data.totalDistanceKm,
                    onBack = { viewModel.handleIntent(StatisticsIntent.CloseChart) }
                )
            }
        }
    }

    // Achievements Dialog
    if (state.showAchievements) {
        Dialog(
            onDismissRequest = { viewModel.handleIntent(StatisticsIntent.HideAchievements) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AchievementsDialog(
                onClose = { viewModel.handleIntent(StatisticsIntent.HideAchievements) }
            )
        }
    }
}

@Composable
fun StreakCard(
    currentStreak: Int,
    longestStreak: Int,
    onAchievementsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = onAchievementsClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = SageGreen
                    )
                    Text(
                        text = "🔥 Серия шагов",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Текущая: $currentStreak дней",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Рекорд: $longestStreak дней",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "View achievements",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun AchievementsDialog(
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏆 Достижения",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    androidx.compose.material3.IconButton(onClick = onClose) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Полноэкранный экран достижений скоро будет доступен!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Тут вы сможете видеть все свои достижения и прогресс.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
