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
import ru.diaries.mydiaries.domain.usecase.statistics.DailyStepsPoint
import ru.diaries.mydiaries.ui.theme.SageGreen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsChartScreen(
    dailySteps: List<DailyStepsPoint>,
    totalSteps: Int,
    onBack: () -> Unit
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("ru", "RU"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.statistics_steps_month)) },
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
                    text = stringResource(R.string.chart_total_steps_format, numberFormat.format(totalSteps)),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (dailySteps.isNotEmpty()) {
                    val avgSteps = totalSteps / dailySteps.size
                    Text(
                        text = stringResource(R.string.chart_avg_steps_per_day, numberFormat.format(avgSteps)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                BarChart(
                    entries = dailySteps.map { point ->
                        BarChartEntry(
                            label = point.date.dayOfMonth.toString(),
                            value = point.steps.toFloat(),
                            color = SageGreen
                        )
                    },
                    barColor = SageGreen,
                    goalLine = 10_000f,
                    goalLineColor = MaterialTheme.colorScheme.error,
                    valueFormatter = { numberFormat.format(it.toInt()) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
