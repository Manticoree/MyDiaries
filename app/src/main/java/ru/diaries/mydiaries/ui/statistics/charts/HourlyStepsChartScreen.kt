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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.diaries.mydiaries.R
import ru.diaries.mydiaries.service.StepCounterService
import ru.diaries.mydiaries.ui.theme.SageGreen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HourlyStepsChartScreen(
    onBack: () -> Unit
) {
    val hourlySteps by StepCounterService.hourlySteps.collectAsState()
    val totalSteps by StepCounterService.todaySteps.collectAsState()
    val numberFormat = NumberFormat.getNumberInstance(Locale("ru", "RU"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.hourly_steps_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
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
                    text = stringResource(
                        R.string.hourly_steps_total,
                        numberFormat.format(totalSteps)
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                val entries = (0..23).map { hour ->
                    BarChartEntry(
                        label = String.format("%02d", hour),
                        value = (hourlySteps[hour] ?: 0).toFloat(),
                        color = SageGreen
                    )
                }

                BarChart(
                    entries = entries,
                    barColor = SageGreen,
                    valueFormatter = { numberFormat.format(it.toInt()) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
