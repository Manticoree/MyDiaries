package ru.diaries.mydiaries.feature.workout.ui.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.diaries.mydiaries.feature.workout.ui.browser.ExerciseBrowserScreen
import ru.diaries.mydiaries.feature.workout.ui.list.WorkoutListScreen

private val CoralOrange = Color(0xFFE8845C)
private val DeepPurple = Color(0xFF7B68AE)
private val CatalogBlue = Color(0xFF42A5F5)

private enum class HubDestination {
    NONE, MY_WORKOUTS, EXERCISE_BROWSER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutHubScreen(
    onDismiss: () -> Unit
) {
    var currentDestination by remember { mutableStateOf(HubDestination.NONE) }

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
                    title = { Text("Тренировки") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Выберите раздел",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // My Workouts
                HubCard(
                    title = "Мои тренировки",
                    subtitle = "История, программы, статистика",
                    icon = Icons.AutoMirrored.Outlined.List,
                    gradientColors = listOf(CoralOrange, CoralOrange.copy(alpha = 0.7f)),
                    onClick = { currentDestination = HubDestination.MY_WORKOUTS }
                )

                // AI Workout
                HubCard(
                    title = "AI-тренировка",
                    subtitle = "Генерация тренировки с помощью Gemini AI",
                    icon = Icons.Default.AutoAwesome,
                    gradientColors = listOf(DeepPurple, DeepPurple.copy(alpha = 0.7f)),
                    onClick = { currentDestination = HubDestination.MY_WORKOUTS }
                )

                // Exercise Catalog
                HubCard(
                    title = "Каталог упражнений",
                    subtitle = "8 категорий, картинки, поиск из wger.de",
                    icon = Icons.Outlined.Search,
                    gradientColors = listOf(CatalogBlue, CatalogBlue.copy(alpha = 0.7f)),
                    onClick = { currentDestination = HubDestination.EXERCISE_BROWSER }
                )
            }
        }
    }

    // Sub-screens
    when (currentDestination) {
        HubDestination.MY_WORKOUTS -> {
            WorkoutListScreen(
                onDismiss = { currentDestination = HubDestination.NONE }
            )
        }
        HubDestination.EXERCISE_BROWSER -> {
            ExerciseBrowserScreen(
                onDismiss = { currentDestination = HubDestination.NONE }
            )
        }
        HubDestination.NONE -> { /* hub is shown */ }
    }
}

@Composable
private fun HubCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(gradientColors)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
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
