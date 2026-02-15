package ru.diaries.mydiaries.feature.track.ui

import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import ru.diaries.mydiaries.feature.track.data.model.LocationPoint
import ru.diaries.mydiaries.feature.track.data.model.PlaceType
import ru.diaries.mydiaries.feature.track.data.model.VisitedPlace

@Composable
fun OsmMapView(
    points: List<LocationPoint>,
    modifier: Modifier = Modifier,
    markers: List<VisitedPlace> = emptyList(),
    isInteractive: Boolean = true,
    lineColor: Color = Color(0xFF6B8E6B),
    lineWidth: Float = 5f
) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(isInteractive)
            isTilesScaledToDpi = true
        }
    }

    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { map ->
            map.setMultiTouchControls(isInteractive)
            map.isEnabled = isInteractive

            map.overlays.clear()

            if (points.size >= 2) {
                val polyline = Polyline().apply {
                    outlinePaint.color = lineColor.toArgb()
                    outlinePaint.strokeWidth = lineWidth * context.resources.displayMetrics.density
                    outlinePaint.strokeCap = Paint.Cap.ROUND
                    outlinePaint.strokeJoin = Paint.Join.ROUND
                    outlinePaint.isAntiAlias = true

                    setPoints(points.map { GeoPoint(it.latitude, it.longitude) })
                }
                map.overlays.add(polyline)

                val boundingBox = BoundingBox.fromGeoPoints(
                    points.map { GeoPoint(it.latitude, it.longitude) }
                )
                map.post {
                    try {
                        map.zoomToBoundingBox(boundingBox, true, 60)
                    } catch (_: Exception) {
                        map.controller.setCenter(
                            GeoPoint(points.first().latitude, points.first().longitude)
                        )
                        map.controller.setZoom(15.0)
                    }
                }
            } else if (points.size == 1) {
                map.controller.setCenter(
                    GeoPoint(points.first().latitude, points.first().longitude)
                )
                map.controller.setZoom(15.0)
            }

            val density = context.resources.displayMetrics.density
            val markerSizePx = (14 * density).toInt()

            for (place in markers) {
                val color = when (place.type) {
                    PlaceType.START -> android.graphics.Color.rgb(76, 175, 80)
                    PlaceType.STOP -> android.graphics.Color.rgb(255, 152, 0)
                    PlaceType.END -> android.graphics.Color.rgb(244, 67, 54)
                }

                val circleDrawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                    setStroke((2 * density).toInt(), android.graphics.Color.WHITE)
                    setSize(markerSizePx, markerSizePx)
                }

                val marker = Marker(map).apply {
                    position = GeoPoint(place.latitude, place.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    icon = circleDrawable
                    title = place.name ?: when (place.type) {
                        PlaceType.START -> "Старт"
                        PlaceType.STOP -> "Остановка"
                        PlaceType.END -> "Финиш"
                    }
                }
                map.overlays.add(marker)
            }

            map.invalidate()
        }
    )
}
