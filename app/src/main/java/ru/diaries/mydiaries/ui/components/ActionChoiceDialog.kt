package ru.diaries.mydiaries.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.diaries.mydiaries.R
import ru.diaries.mydiaries.ui.theme.GoldenHoney
import ru.diaries.mydiaries.ui.theme.GreetingStyle
import ru.diaries.mydiaries.ui.theme.LavenderMist
import ru.diaries.mydiaries.ui.theme.SageGreen
import ru.diaries.mydiaries.ui.theme.Terracotta

@Composable
fun ActionChoiceDialog(
    onAddEntry: () -> Unit,
    onAddExpense: () -> Unit,
    onAddTask: () -> Unit,
    onDismiss: () -> Unit
) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 250,
                easing = FastOutSlowInEasing
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .graphicsLayer {
                    val progress = animationProgress.value
                    alpha = progress
                    scaleX = 0.9f + (0.1f * progress)
                    scaleY = 0.9f + (0.1f * progress)
                },
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Decorative header with book icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoStories,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.choose_action),
                    style = GreetingStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action cards - all shown together, no staggered animation
                ActionItemCard(
                    icon = Icons.Outlined.Edit,
                    title = stringResource(R.string.new_entry),
                    subtitle = stringResource(R.string.new_entry_description),
                    gradientColors = listOf(SageGreen, LavenderMist),
                    onClick = {
                        onDismiss()
                        onAddEntry()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ActionItemCard(
                    icon = Icons.Outlined.AttachMoney,
                    title = stringResource(R.string.add_expense),
                    subtitle = stringResource(R.string.add_expense_description),
                    gradientColors = listOf(Terracotta, GoldenHoney),
                    onClick = {
                        onDismiss()
                        onAddExpense()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ActionItemCard(
                    icon = Icons.Outlined.TaskAlt,
                    title = stringResource(R.string.add_task),
                    subtitle = stringResource(R.string.add_task_description),
                    gradientColors = listOf(SageGreen, LavenderMist),
                    onClick = {
                        onDismiss()
                        onAddTask()
                    }
                )
            }
        }
    }
}

@Composable
private fun ActionItemCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradientColors: List<androidx.compose.ui.graphics.Color>,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = if (isPressed) 0.98f else 1f
                scaleY = if (isPressed) 0.98f else 1f
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = true,
                    color = gradientColors.first()
                ),
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gradient icon background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(colors = gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.surface
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
