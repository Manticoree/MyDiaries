package ru.diaries.mydiaries.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import ru.diaries.mydiaries.R
import ru.diaries.mydiaries.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onAchievementClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profile_title),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = GoldenHoney,
                    strokeWidth = 3.dp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Profile Header with Level System
                EnhancedProfileHeader(
                    userName = state.userProfile?.userName ?: "",
                    level = calculateLevel(state.userProfile?.totalSteps ?: 0),
                    experience = state.userProfile?.totalSteps ?: 0,
                    isEditingName = state.isEditingName,
                    newUserName = state.newUserName,
                    onEditClick = { viewModel.handleIntent(ProfileIntent.StartEditingName) },
                    onCancelClick = { viewModel.handleIntent(ProfileIntent.CancelEditingName) },
                    onSaveClick = { viewModel.handleIntent(ProfileIntent.SaveName) },
                    onNameChange = { viewModel.handleIntent(ProfileIntent.UpdateName(it)) }
                )

                // Quick Stats Row
                QuickStatsRow(
                    totalSteps = state.userProfile?.totalSteps ?: 0,
                    currentStreak = state.userProfile?.currentStreak ?: 0,
                    longestStreak = state.userProfile?.longestStreak ?: 0
                )

                // Progress Cards
                ProgressSection(
                    totalSteps = state.userProfile?.totalSteps ?: 0,
                    currentStreak = state.userProfile?.currentStreak ?: 0
                )

                // Achievements Card with Progress
                EnhancedAchievementsCard(
                    unlockedCount = state.unlockedAchievements.size,
                    totalCount = 100,
                    onAchievementClick = onAchievementClick
                )

                // Theme Settings Card
                ThemeSettingsCard(
                    currentTheme = state.selectedTheme,
                    onThemeClick = { viewModel.handleIntent(ProfileIntent.ShowThemeDialog) }
                )

                // Recent Achievements with Animation
                if (state.unlockedAchievements.isNotEmpty()) {
                    AnimatedRecentAchievements(
                        achievements = state.unlockedAchievements.take(5),
                        onAchievementClick = onAchievementClick
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Theme Selection Dialog
    if (state.showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = state.selectedTheme,
            onThemeSelected = { viewModel.handleIntent(ProfileIntent.SetTheme(it)) },
            onDismiss = { viewModel.handleIntent(ProfileIntent.HideThemeDialog) }
        )
    }
}

// Calculate level based on total steps
private fun calculateLevel(totalSteps: Long): Int {
    return ((totalSteps / 10000) + 1).toInt()
}

// Calculate experience needed for next level
private fun experienceForNextLevel(level: Int): Long {
    return level * 10000L
}

@Composable
fun EnhancedProfileHeader(
    userName: String,
    level: Int,
    experience: Long,
    isEditingName: Boolean,
    newUserName: String,
    onEditClick: () -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    onNameChange: (String) -> Unit
) {
    val avatarScale by animateFloatAsState(
        targetValue = if (isEditingName) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "avatarScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = GoldenHoney.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar with Gradient and Level Badge
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        spotColor = GoldenHoney.copy(alpha = 0.4f)
                    )
            ) {
                // Gradient Avatar Background
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    GoldenHoney,
                                    Terracotta,
                                    WarmBrown
                                ),
                                radius = 100f,
                                tileMode = TileMode.Clamp
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏃",
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier.scale(avatarScale)
                    )
                }

                // Level Badge
                Box(
                    modifier = Modifier
                        .offset(x = 40.dp, y = 40.dp)
                        .size(32.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = CircleShape,
                            spotColor = MaterialTheme.colorScheme.primary
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    GoldenHoney,
                                    Terracotta
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$level",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Experience Bar
            val experienceForCurrentLevel = experienceForNextLevel(level - 1)
            val experienceForNextLevel = experienceForNextLevel(level)
            val progress = ((experience - experienceForCurrentLevel).toFloat() /
                    (experienceForNextLevel - experienceForCurrentLevel)).coerceIn(0f, 1f)

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Уровень $level",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Уровень ${level + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = GoldenHoney,
                        trackColor = Color.Transparent,
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${NumberFormat.getNumberInstance(Locale("ru", "RU")).format(experience)} / ${NumberFormat.getNumberInstance(Locale("ru", "RU")).format(experienceForNextLevel)} опыта",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            if (isEditingName) {
                OutlinedTextField(
                    value = newUserName,
                    onValueChange = onNameChange,
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.enter_name_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onCancelClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.cancel),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(
                        onClick = onSaveClick,
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        SageGreen,
                                        LavenderMist
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Text(
                            stringResource(R.string.save),
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (userName.isNotEmpty()) userName else stringResource(R.string.guest_user),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        onClick = onEditClick,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit name",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickStatsRow(
    totalSteps: Long,
    currentStreak: Int,
    longestStreak: Int
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("ru", "RU"))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MiniStatCard(
            modifier = Modifier.weight(1f),
            title = "Шаги",
            value = numberFormat.format(totalSteps),
            icon = "👣",
            color = SageGreen
        )
        MiniStatCard(
            modifier = Modifier.weight(1f),
            title = "Серия",
            value = "$currentStreak",
            icon = "🔥",
            color = Terracotta
        )
        MiniStatCard(
            modifier = Modifier.weight(1f),
            title = "Рекорд",
            value = "$longestStreak",
            icon = "🏆",
            color = GoldenHoney
        )
    }
}

@Composable
fun MiniStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ProgressSection(
    totalSteps: Long,
    currentStreak: Int
) {
    val dailyGoal = 10000L
    val dailyProgress = (totalSteps % dailyGoal).toFloat() / dailyGoal
    val streakProgress = (currentStreak % 7).toFloat() / 7f

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily Steps Progress
        ProgressCard(
            title = "Сегодня",
            subtitle = "${(totalSteps % dailyGoal)} из $dailyGoal шагов",
            progress = dailyProgress,
            icon = "🎯",
            gradientColors = GradientColors.SoftMeadow
        )

        // Weekly Streak Progress
        ProgressCard(
            title = "Недельная серия",
            subtitle = "$currentStreak ${getStreakLabel(currentStreak)}",
            progress = streakProgress,
            icon = "📅",
            gradientColors = GradientColors.WarmSunrise
        )
    }
}

private fun getStreakLabel(streak: Int): String {
    return when {
        streak % 10 == 1 && streak % 100 != 11 -> "день"
        streak % 10 in 2..4 && (streak % 100 !in 12..14) -> "дня"
        else -> "дней"
    }
}

@Composable
fun ProgressCard(
    title: String,
    subtitle: String,
    progress: Float,
    icon: String,
    gradientColors: List<Color>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = gradientColors[0].copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = icon,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = gradientColors[0]
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = gradientColors[0],
                    trackColor = Color.Transparent,
                )
            }
        }
    }
}

@Composable
fun EnhancedAchievementsCard(
    unlockedCount: Int,
    totalCount: Int,
    onAchievementClick: () -> Unit
) {
    val progress = unlockedCount.toFloat() / totalCount

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = GoldenHoney.copy(alpha = 0.3f)
            )
            .clickable(onClick = onAchievementClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    GoldenHoney.copy(alpha = 0.3f),
                    Terracotta.copy(alpha = 0.3f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        GoldenHoney,
                                        Terracotta
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🏆",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.achievements),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$unlockedCount из $totalCount",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ExpandMore,
                        contentDescription = "Open achievements",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Прогресс достижений",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = GoldenHoney
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = GoldenHoney,
                    trackColor = Color.Transparent,
                )
            }
        }
    }
}

@Composable
fun AnimatedRecentAchievements(
    achievements: List<ru.diaries.mydiaries.data.model.Achievement>,
    onAchievementClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = GoldenHoney,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(R.string.recent_achievements),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            achievements.forEachIndexed { index, achievement ->
                AnimatedAchievementItem(
                    achievement = achievement,
                    index = index
                )
            }
        }
    }
}

@Composable
fun AnimatedAchievementItem(
    achievement: ru.diaries.mydiaries.data.model.Achievement,
    index: Int
) {
    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "achievementScale"
    )

    AnimatedVisibility(
        visible = true,
        enter = expandVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            expandFrom = Alignment.Top
        ) + fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = shrinkVertically() + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(animatedScale),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon with gradient background
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = GoldenHoney.copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    SageGreen.copy(alpha = 0.3f),
                                    LavenderMist.copy(alpha = 0.2f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = achievement.icon,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = achievement.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = achievement.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Checkmark icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            SageGreen.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = SageGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeSettingsCard(
    currentTheme: AppTheme,
    onThemeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onThemeClick)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = LavenderMist.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                LavenderMist.copy(alpha = 0.7f),
                                GoldenHoney.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎨",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Тема приложения",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentTheme.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Icon(
                Icons.Default.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Выберите тему",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    AppTheme.entries.forEach { theme ->
                        ThemeOptionRow(
                            theme = theme,
                            isSelected = theme == currentTheme,
                            onClick = { onThemeSelected(theme) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Отмена")
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeOptionRow(
    theme: AppTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) {
            SageGreen.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (theme) {
                AppTheme.SYSTEM -> "🌗"
                AppTheme.LIGHT -> "☀️"
                AppTheme.DARK -> "🌙"
            }

            Text(
                text = icon,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = theme.displayName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = SageGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
