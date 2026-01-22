package ru.diaries.mydiaries.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.diaries.mydiaries.data.model.Expense
import ru.diaries.mydiaries.data.model.ExpenseCategory
import java.text.NumberFormat
import java.util.Locale

data class PieChartData(
    val category: ExpenseCategory,
    val amount: Double,
    val percentage: Float
)

@Composable
fun ExpensePieChart(
    expenses: List<Expense>,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    strokeWidth: Dp = 24.dp,
    showLegend: Boolean = true,
    animationDuration: Int = 800
) {
    val total = expenses.sumOf { it.amount }

    if (total == 0.0 || expenses.isEmpty()) {
        EmptyPieChart(
            modifier = modifier,
            size = size,
            strokeWidth = strokeWidth
        )
        return
    }

    val chartData = expenses
        .groupBy { it.category }
        .map { (category, categoryExpenses) ->
            val amount = categoryExpenses.sumOf { it.amount }
            PieChartData(
                category = category,
                amount = amount,
                percentage = (amount / total * 100).toFloat()
            )
        }
        .sortedByDescending { it.amount }

    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(expenses) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationDuration)
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val canvasSize = this.size.minDimension
                val radius = (canvasSize - strokeWidth.toPx()) / 2
                val centerOffset = Offset(canvasSize / 2, canvasSize / 2)

                var startAngle = -90f

                chartData.forEach { data ->
                    val sweepAngle = (data.percentage / 100f) * 360f * animationProgress.value

                    drawArc(
                        color = data.category.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(
                            centerOffset.x - radius,
                            centerOffset.y - radius
                        ),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(
                            width = strokeWidth.toPx(),
                            cap = StrokeCap.Round
                        )
                    )

                    startAngle += sweepAngle
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatCurrency(total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "всего",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (showLegend && chartData.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            PieChartLegend(chartData = chartData)
        }
    }
}

@Composable
private fun EmptyPieChart(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    strokeWidth: Dp = 24.dp
) {
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val canvasSize = this.size.minDimension
                val radius = (canvasSize - strokeWidth.toPx()) / 2
                val centerOffset = Offset(canvasSize / 2, canvasSize / 2)

                drawArc(
                    color = emptyColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(
                        centerOffset.x - radius,
                        centerOffset.y - radius
                    ),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(
                        width = strokeWidth.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }

            Text(
                text = "Нет трат",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PieChartLegend(
    chartData: List<PieChartData>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chartData.forEach { data ->
            LegendItem(
                color = data.category.color,
                categoryName = data.category.displayName,
                amount = data.amount,
                percentage = data.percentage
            )
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    categoryName: String,
    amount: Double,
    percentage: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(12.dp),
            shape = CircleShape,
            color = color
        ) {}

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = categoryName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = formatCurrency(amount),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${percentage.toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MiniPieChart(
    expenses: List<Expense>,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 8.dp
) {
    val total = expenses.sumOf { it.amount }

    if (total == 0.0 || expenses.isEmpty()) {
        val emptyColor = MaterialTheme.colorScheme.surfaceVariant
        Canvas(modifier = modifier.size(size)) {
            val canvasSize = this.size.minDimension
            val radius = (canvasSize - strokeWidth.toPx()) / 2
            val centerOffset = Offset(canvasSize / 2, canvasSize / 2)

            drawArc(
                color = emptyColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        return
    }

    val chartData = expenses
        .groupBy { it.category }
        .map { (category, categoryExpenses) ->
            val amount = categoryExpenses.sumOf { it.amount }
            PieChartData(
                category = category,
                amount = amount,
                percentage = (amount / total * 100).toFloat()
            )
        }
        .sortedByDescending { it.amount }

    Canvas(modifier = modifier.size(size)) {
        val canvasSize = this.size.minDimension
        val radius = (canvasSize - strokeWidth.toPx()) / 2
        val centerOffset = Offset(canvasSize / 2, canvasSize / 2)

        var startAngle = -90f

        chartData.forEach { data ->
            val sweepAngle = (data.percentage / 100f) * 360f

            drawArc(
                color = data.category.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            startAngle += sweepAngle
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    return format.format(amount)
}
