package ru.diaries.mydiaries.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.diaries.mydiaries.R
import ru.diaries.mydiaries.domain.usecase.history.HistoryData
import ru.diaries.mydiaries.ui.theme.GreetingStyle
import ru.diaries.mydiaries.ui.theme.NumberStyle
import ru.diaries.mydiaries.ui.theme.WarmBrown
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AggregateSummaryCard(
    historyData: HistoryData,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getInstance(Locale("ru", "RU"))
    val currencyFormat = NumberFormat.getIntegerInstance(Locale("ru", "RU"))

    val headerGradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        )
    )

    Card(
        modifier = modifier
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerGradient)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = stringResource(R.string.history_your_journey),
                    style = GreetingStyle,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Row 1: days, entries, steps
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        emoji = "\uD83D\uDCC5",
                        value = numberFormat.format(historyData.totalDays),
                        label = stringResource(R.string.history_days),
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        emoji = "\uD83D\uDCD6",
                        value = numberFormat.format(historyData.totalEntries),
                        label = stringResource(R.string.history_entries),
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        emoji = "\uD83D\uDEB6",
                        value = numberFormat.format(historyData.totalSteps),
                        label = stringResource(R.string.history_steps),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Row 2: distance, expenses, calories
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        emoji = "\uD83D\uDDFA\uFE0F",
                        value = String.format(Locale("ru"), "%.1f", historyData.totalDistanceKm),
                        label = stringResource(R.string.history_distance),
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        emoji = "\uD83D\uDCB0",
                        value = currencyFormat.format(historyData.totalExpenses),
                        label = stringResource(R.string.history_expenses),
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        emoji = "\uD83C\uDF54",
                        value = numberFormat.format(historyData.totalCalories),
                        label = stringResource(R.string.history_calories),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = NumberStyle,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
