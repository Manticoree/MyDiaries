package ru.diaries.mydiaries.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.ui.theme.GoldenHoney
import ru.diaries.mydiaries.ui.theme.Parchment
import ru.diaries.mydiaries.ui.theme.Terracotta
import ru.diaries.mydiaries.ui.theme.TextPrimary
import ru.diaries.mydiaries.ui.theme.WarmBrown
import ru.diaries.mydiaries.analytics.EventsManager

data class OnboardingPage(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
    val features: List<String>
)

private val onboardingPages = listOf(
    OnboardingPage(
        icon = Icons.Default.FitnessCenter,
        title = "Тренируйся с умом",
        description = "Отслеживай свои тренировки, прогресс и достигай поставленных целей",
        features = listOf(
            "Запись тренировок с подробной статистикой",
            "500+ упражнений с видео инструкциями",
            "Персональные планы тренировок"
        )
    ),
    OnboardingPage(
        icon = Icons.Default.Restaurant,
        title = "Правильное питание",
        description = "Контролируйте свой рацион и достигайте своих целей",
        features = listOf(
            "Дневник питания с калориями",
            "База продуктов с КБЖУ",
            "Рецепты здоровых блюд"
        )
    ),
    OnboardingPage(
        icon = Icons.Default.SelfImprovement,
        title = "Активность и сон",
        description = "Следите за своим здоровьем круглосуточно",
        features = listOf(
            "Трекинг шагов и активности",
            "Запись сна и настроения",
            "Графики прогресса за месяц"
        )
    )
)

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit
) {
    var currentPage by remember { mutableStateOf(0) }
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { onboardingPages.size }
    )
    val coroutineScope = rememberCoroutineScope()

    // Track onboarding start
    LaunchedEffect(Unit) {
        startTime = System.currentTimeMillis()
    }

    // Track page views and sync with currentPage
    LaunchedEffect(pagerState.currentPage) {
        currentPage = pagerState.currentPage
        // Log page view event
        EventsManager.logOnboardingPageView(currentPage, onboardingPages.size)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Parchment,
                        Color.White,
                        Parchment
                    )
                )
            )
    ) {
        // Top progress indicator
        LinearProgressIndicator(
            progress = { (currentPage + 1).toFloat() / onboardingPages.size },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            color = WarmBrown,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Skip button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                if (currentPage < onboardingPages.size - 1) {
                    TextButton(onClick = {
                        EventsManager.logOnboardingSkipped(currentPage)
                        onGetStarted()
                    }) {
                        Text(
                            text = "Пропустить",
                            color = TextPrimary.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(
                    page = onboardingPages[page],
                    isLastPage = page == onboardingPages.size - 1
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Page indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                onboardingPages.forEachIndexed { index, _ ->
                    val isSelected = index == currentPage
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.8f,
                        animationSpec = tween(300),
                        label = "indicator_scale"
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) WarmBrown else WarmBrown.copy(alpha = 0.3f)
                            )
                            .scale(scale)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentPage < onboardingPages.size - 1) {
                    // Next button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(currentPage + 1)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarmBrown
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Далее",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null
                        )
                    }
                } else {
                    // Last page - single button
                    Button(
                        onClick = {
                            val duration = (System.currentTimeMillis() - startTime) / 1000
                            EventsManager.logOnboardingCompleted(duration)
                            onGetStarted()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Terracotta
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Начать",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    isLastPage: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon with glow
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            // Glow effect
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                WarmBrown.copy(alpha = 0.2f),
                                WarmBrown.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Icon background
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        if (isLastPage) {
                            Brush.linearGradient(
                                colors = listOf(GoldenHoney, Terracotta)
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(WarmBrown, Terracotta)
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = TextPrimary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Features list
        page.features.forEach { feature ->
            FeatureItem(text = feature)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun FeatureItem(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = WarmBrown.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = WarmBrown
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
