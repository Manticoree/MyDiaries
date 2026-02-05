package ru.diaries.mydiaries.ui.statistics.charts

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.diaries.mydiaries.R
import ru.diaries.mydiaries.domain.usecase.statistics.DailyCaloriesPoint
import ru.diaries.mydiaries.ui.theme.GoldenHoney
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaloriesChartScreen(
    dailyCalories: List<DailyCaloriesPoint>,
    totalCalories: Int,
    onBack: () -> Unit
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("ru", "RU"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.statistics_calories_month)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.chart_total_kcal_format, numberFormat.format(totalCalories)),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (dailyCalories.isNotEmpty()) {
                    val avgCalories = totalCalories / dailyCalories.size
                    Text(
                        text = stringResource(R.string.chart_avg_kcal_per_day, numberFormat.format(avgCalories)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                BarChart(
                    entries = dailyCalories.map { point ->
                        BarChartEntry(
                            label = point.date.dayOfMonth.toString(),
                            value = point.calories.toFloat(),
                            color = GoldenHoney
                        )
                    },
                    barColor = GoldenHoney,
                    valueFormatter = { numberFormat.format(it.toInt()) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
