package ru.diaries.mydiaries.feature.track.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.diaries.mydiaries.feature.track.data.model.DailyTrack

@Composable
fun TrackStatsRow(
    track: DailyTrack,
    modifier: Modifier = Modifier,
    distanceLabel: String = "Расстояние",
    durationLabel: String = "Длительность",
    speedLabel: String = "Ср. скорость",
    stepsLabel: String = "Шаги",
    kmUnit: String = "км",
    kmhUnit: String = "км/ч"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = stepsLabel,
                value = formatSteps(track.steps),
                unit = ""
            )
            StatItem(
                label = distanceLabel,
                value = formatDistance(track.distanceMeters),
                unit = kmUnit
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = durationLabel,
                value = formatDuration(track.durationMillis),
                unit = ""
            )
            StatItem(
                label = speedLabel,
                value = String.format("%.1f", track.averageSpeedKmh),
                unit = kmhUnit
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    unit: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (unit.isNotEmpty()) "$value $unit" else value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatSteps(steps: Int): String {
    return java.text.NumberFormat.getInstance().format(steps)
}

private fun formatDistance(meters: Double): String {
    return if (meters < 1000) {
        "${meters.toInt()} м"
    } else {
        String.format("%.2f", meters / 1000.0)
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}
