package ru.diaries.mydiaries.feature.track.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.diaries.mydiaries.feature.track.data.model.DailyTrack
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenMapDialog(
    track: DailyTrack,
    isTracking: Boolean,
    onToggleTracking: () -> Unit,
    onDismiss: () -> Unit,
    titleText: String = "Карта маршрута",
    startTrackingText: String = "Начать маршрут",
    stopTrackingText: String = "Остановить",
    distanceLabel: String = "Расстояние",
    durationLabel: String = "Длительность",
    speedLabel: String = "Ср. скорость",
    stepsLabel: String = "Шаги",
    kmUnit: String = "км",
    kmhUnit: String = "км/ч"
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = titleText,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = track.date.format(DateTimeFormatter.ofPattern("d MMMM yyyy")),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Interactive map filling screen
                OsmMapView(
                    points = track.points,
                    isInteractive = true,
                    modifier = Modifier.fillMaxSize()
                )

                // Bottom overlay with stats
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            TrackStatsRow(
                                track = track,
                                distanceLabel = distanceLabel,
                                durationLabel = durationLabel,
                                speedLabel = speedLabel,
                                stepsLabel = stepsLabel,
                                kmUnit = kmUnit,
                                kmhUnit = kmhUnit
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = onToggleTracking,
                                modifier = Modifier.fillMaxWidth(),
                                colors = if (isTracking) {
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                } else {
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (isTracking) stopTrackingText else startTrackingText
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
