package ru.diaries.mydiaries.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.diaries.mydiaries.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun DateGroupHeader(
    date: LocalDate,
    entryCount: Int,
    expenseCount: Int = 0,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "expand_icon_rotation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "press_scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isExpanded) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(durationMillis = 300),
        label = "background_color"
    )

    val iconBackgroundColor by animateColorAsState(
        targetValue = if (isExpanded) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
        },
        animationSpec = tween(durationMillis = 300),
        label = "icon_background_color"
    )

    val iconTint by animateColorAsState(
        targetValue = if (isExpanded) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 300),
        label = "icon_tint"
    )

    val elevation by animateDpAsState(
        targetValue = if (isExpanded) 2.dp else 1.dp,
        animationSpec = tween(durationMillis = 300),
        label = "elevation"
    )

    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
        .withLocale(Locale.getDefault())

    val countText = buildCountText(entryCount, expenseCount)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    color = MaterialTheme.colorScheme.primary
                ),
                onClick = onClick
            ),
        color = backgroundColor,
        tonalElevation = elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = date.format(dateFormatter),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = countText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded)
                        stringResource(R.string.collapse)
                    else
                        stringResource(R.string.expand),
                    tint = iconTint,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer {
                            rotationZ = rotation
                        }
                )
            }
        }
    }
}

@Composable
private fun buildCountText(entryCount: Int, expenseCount: Int): String {
    val parts = mutableListOf<String>()

    if (entryCount > 0) {
        val entryText = when {
            entryCount == 1 -> stringResource(R.string.entry_count_one)
            entryCount in 2..4 -> stringResource(R.string.entry_count_few, entryCount)
            else -> stringResource(R.string.entry_count_many, entryCount)
        }
        parts.add(entryText)
    }

    if (expenseCount > 0) {
        val expenseText = when {
            expenseCount == 1 -> stringResource(R.string.expense_count_one)
            expenseCount in 2..4 -> stringResource(R.string.expense_count_few, expenseCount)
            else -> stringResource(R.string.expense_count_many, expenseCount)
        }
        parts.add(expenseText)
    }

    return if (parts.isEmpty()) {
        stringResource(R.string.no_items)
    } else {
        parts.joinToString(", ")
    }
}
