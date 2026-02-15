package ru.diaries.mydiaries.feature.track.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.diaries.mydiaries.feature.track.data.NominatimGeocoderService
import ru.diaries.mydiaries.feature.track.data.StopDetector
import ru.diaries.mydiaries.feature.track.data.model.DailyTrack
import ru.diaries.mydiaries.feature.track.data.model.PlaceType
import ru.diaries.mydiaries.feature.track.data.model.VisitedPlace
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

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
    kmhUnit: String = "км/ч",
    showTrackingButton: Boolean = true,
    visitedPlacesHeader: String = "Посещённые места",
    loadingText: String = "Загрузка..."
) {
    var places by remember { mutableStateOf<List<VisitedPlace>>(emptyList()) }
    var isLoadingPlaces by remember { mutableStateOf(true) }

    LaunchedEffect(track.points) {
        isLoadingPlaces = true
        val detected = StopDetector.detectPlaces(track.points)
        places = detected

        val resolved = detected.toMutableList()
        for (i in resolved.indices) {
            val place = resolved[i]
            if (place.name == null) {
                val name = NominatimGeocoderService.reverseGeocode(
                    place.latitude,
                    place.longitude
                )
                resolved[i] = place.copy(name = name)
                places = resolved.toList()
            }
        }
        isLoadingPlaces = false
    }

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                OsmMapView(
                    points = track.points,
                    markers = places,
                    isInteractive = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.55f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.45f)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

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
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = visitedPlacesHeader,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (isLoadingPlaces) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            places,
                            key = { "${it.latitude}_${it.longitude}_${it.type}" }
                        ) { place ->
                            PlaceItem(
                                place = place,
                                loadingText = loadingText
                            )
                        }
                    }

                    if (showTrackingButton) {
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

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceItem(
    place: VisitedPlace,
    loadingText: String
) {
    val markerColor = when (place.type) {
        PlaceType.START -> Color(0xFF4CAF50)
        PlaceType.STOP -> Color(0xFFFF9800)
        PlaceType.END -> Color(0xFFF44336)
    }

    val typeLabel = when (place.type) {
        PlaceType.START -> "Старт"
        PlaceType.STOP -> "Остановка"
        PlaceType.END -> "Финиш"
    }

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(markerColor)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = place.name ?: loadingText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (place.name != null) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Text(
                text = buildString {
                    append(typeLabel)
                    append(" \u2022 ")
                    append(timeFormat.format(Date(place.arrivalTime)))
                    if (place.arrivalTime != place.departureTime) {
                        append(" \u2013 ")
                        append(timeFormat.format(Date(place.departureTime)))
                    }
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
