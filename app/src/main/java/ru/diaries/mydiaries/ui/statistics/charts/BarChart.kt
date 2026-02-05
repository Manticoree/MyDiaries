package ru.diaries.mydiaries.ui.statistics.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BarChartEntry(
    val label: String,
    val value: Float,
    val color: Color? = null
)

@Composable
fun BarChart(
    entries: List<BarChartEntry>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    height: Dp = 220.dp,
    goalLine: Float? = null,
    goalLineColor: Color = MaterialTheme.colorScheme.error,
    valueFormatter: (Float) -> String = { it.toInt().toString() },
    animationDuration: Int = 800
) {
    val animationProgress = remember { Animatable(0f) }
    val textMeasurer = rememberTextMeasurer()
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

    LaunchedEffect(entries) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationDuration)
        )
    }

    if (entries.isEmpty()) return

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val leftPadding = 48.dp.toPx()
        val bottomPadding = 28.dp.toPx()
        val topPadding = 16.dp.toPx()
        val chartWidth = size.width - leftPadding - 8.dp.toPx()
        val chartHeight = size.height - bottomPadding - topPadding

        val maxValue = maxOf(
            entries.maxOf { it.value },
            goalLine ?: 0f
        ).let { if (it == 0f) 1f else it }

        val barWidth = (chartWidth / entries.size) * 0.7f
        val barSpacing = chartWidth / entries.size

        drawYAxis(
            textMeasurer = textMeasurer,
            maxValue = maxValue,
            chartHeight = chartHeight,
            leftPadding = leftPadding,
            topPadding = topPadding,
            gridColor = gridColor,
            labelColor = labelColor,
            chartWidth = chartWidth,
            valueFormatter = valueFormatter
        )

        entries.forEachIndexed { index, entry ->
            val barHeight = (entry.value / maxValue) * chartHeight * animationProgress.value
            val x = leftPadding + index * barSpacing + (barSpacing - barWidth) / 2
            val y = topPadding + chartHeight - barHeight

            drawRect(
                color = entry.color ?: barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )

            if (entries.size <= 15 || index % 2 == 0) {
                val labelStyle = TextStyle(
                    fontSize = 9.sp,
                    color = labelColor
                )
                val labelLayout = textMeasurer.measure(entry.label, labelStyle)
                drawText(
                    textLayoutResult = labelLayout,
                    topLeft = Offset(
                        x + barWidth / 2 - labelLayout.size.width / 2,
                        topPadding + chartHeight + 4.dp.toPx()
                    )
                )
            }
        }

        if (goalLine != null && goalLine > 0f) {
            val goalY = topPadding + chartHeight - (goalLine / maxValue) * chartHeight
            drawLine(
                color = goalLineColor,
                start = Offset(leftPadding, goalY),
                end = Offset(leftPadding + chartWidth, goalY),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
            )
            val goalText = valueFormatter(goalLine)
            val goalStyle = TextStyle(fontSize = 9.sp, color = goalLineColor)
            val goalLayout = textMeasurer.measure(goalText, goalStyle)
            drawText(
                textLayoutResult = goalLayout,
                topLeft = Offset(leftPadding + chartWidth + 2.dp.toPx(), goalY - goalLayout.size.height / 2)
            )
        }
    }
}

private fun DrawScope.drawYAxis(
    textMeasurer: TextMeasurer,
    maxValue: Float,
    chartHeight: Float,
    leftPadding: Float,
    topPadding: Float,
    gridColor: Color,
    labelColor: Color,
    chartWidth: Float,
    valueFormatter: (Float) -> String
) {
    val tickCount = 4
    for (i in 0..tickCount) {
        val value = maxValue * i / tickCount
        val y = topPadding + chartHeight - (chartHeight * i / tickCount)

        drawLine(
            color = gridColor,
            start = Offset(leftPadding, y),
            end = Offset(leftPadding + chartWidth, y),
            strokeWidth = 0.5.dp.toPx()
        )

        val labelStyle = TextStyle(fontSize = 9.sp, color = labelColor)
        val labelLayout = textMeasurer.measure(valueFormatter(value), labelStyle)
        drawText(
            textLayoutResult = labelLayout,
            topLeft = Offset(leftPadding - labelLayout.size.width - 4.dp.toPx(), y - labelLayout.size.height / 2)
        )
    }
}
