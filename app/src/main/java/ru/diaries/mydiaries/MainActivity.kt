package ru.diaries.mydiaries

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import ru.diaries.mydiaries.service.StepCounterService
import ru.diaries.mydiaries.ui.history.HistoryScreen
import ru.diaries.mydiaries.ui.permissions.PermissionsScreen
import ru.diaries.mydiaries.ui.permissions.checkAllPermissionsGranted
import ru.diaries.mydiaries.ui.profile.ProfileScreen
import ru.diaries.mydiaries.ui.splash.SplashScreen
import ru.diaries.mydiaries.ui.statistics.StatisticsScreen
import ru.diaries.mydiaries.ui.statistics.charts.HourlyStepsChartScreen
import ru.diaries.mydiaries.ui.achievements.AchievementsScreen
import ru.diaries.mydiaries.ui.theme.MyDiariesTheme
import ru.diaries.mydiaries.ui.features.FeaturesScreen
import ru.diaries.mydiaries.ui.timeline.TimelineScreen
import androidx.hilt.navigation.compose.hiltViewModel
import ru.diaries.mydiaries.ui.onboarding.OnboardingScreen
import ru.diaries.mydiaries.data.PreferencesManager
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var keepSplashOnScreen = true
    private val showHourlyStepsChart = mutableStateOf(false)
    private val showAchievements = mutableStateOf(false)
    private val showOnboarding = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before super.onCreate
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkStepChartIntent(intent)

        setContent {
            MyDiariesTheme {
                var showSplash by rememberSaveable { mutableStateOf(true) }
                var showPermissions by rememberSaveable {
                    mutableStateOf(!checkAllPermissionsGranted(this@MainActivity))
                }

                // Check if onboarding should be shown
                val showOnboardingScreen by rememberSaveable(preferencesManager.isOnboardingCompleted) {
                    mutableStateOf(!preferencesManager.isOnboardingCompleted)
                }

                // Set install date if not set
                LaunchedEffect(Unit) {
                    if (preferencesManager.installDate == 0L) {
                        preferencesManager.installDate = System.currentTimeMillis()
                    }
                }

                // Dismiss system splash screen when compose is ready
                keepSplashOnScreen = false

                AnimatedVisibility(
                    visible = showSplash,
                    enter = fadeIn(),
                    exit = fadeOut(animationSpec = tween(500))
                ) {
                    SplashScreen(
                        onSplashFinished = {
                            showSplash = false
                        }
                    )
                }

                AnimatedVisibility(
                    visible = !showSplash,
                    enter = fadeIn(animationSpec = tween(500)),
                    exit = fadeOut()
                ) {
                    // Show onboarding if not completed
                    if (showOnboardingScreen) {
                        OnboardingScreen(
                            onGetStarted = {
                                // Mark onboarding as completed
                                preferencesManager.isOnboardingCompleted = true
                            }
                        )
                    }
                    // Show permissions if not granted
                    else if (showPermissions) {
                        PermissionsScreen(
                            onAllPermissionsGranted = {
                                showPermissions = false
                            },
                            onSkip = {
                                showPermissions = false
                            }
                        )
                    } else {
                        var selectedTab by rememberSaveable { mutableIntStateOf(0) }

                        Scaffold(
                            bottomBar = {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = NavigationBarDefaults.Elevation
                                ) {
                                    val navItemColors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 0,
                                        onClick = { selectedTab = 0 },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.Home,
                                                contentDescription = null
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = stringResource(R.string.tab_timeline),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        colors = navItemColors
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 1,
                                        onClick = { selectedTab = 1 },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.DateRange,
                                                contentDescription = null
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = stringResource(R.string.tab_history),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        colors = navItemColors
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 2,
                                        onClick = { selectedTab = 2 },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Outlined.Widgets,
                                                contentDescription = null
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = "Функции",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        colors = navItemColors
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 3,
                                        onClick = { selectedTab = 3 },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Outlined.BarChart,
                                                contentDescription = null
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = stringResource(R.string.tab_statistics),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        colors = navItemColors
                                    )
                                    NavigationBarItem(
                                        selected = selectedTab == 4,
                                        onClick = { selectedTab = 4 },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = "Профиль",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        colors = navItemColors
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (selectedTab) {
                                    0 -> TimelineScreen(
                                        showFab = true,
                                        onStepCardClick = { showHourlyStepsChart.value = true }
                                    )
                                    1 -> HistoryScreen()
                                    2 -> FeaturesScreen()
                                    3 -> StatisticsScreen()
                                    4 -> ProfileScreen(
                                        onAchievementClick = { showAchievements.value = true }
                                    )
                                }
                            }
                        }

                        if (showHourlyStepsChart.value) {
                            Dialog(
                                onDismissRequest = { showHourlyStepsChart.value = false },
                                properties = DialogProperties(
                                    usePlatformDefaultWidth = false,
                                    decorFitsSystemWindows = false
                                )
                            ) {
                                HourlyStepsChartScreen(
                                    onBack = { showHourlyStepsChart.value = false }
                                )
                            }
                        }

                        if (showAchievements.value) {
                            Dialog(
                                onDismissRequest = { showAchievements.value = false },
                                properties = DialogProperties(
                                    usePlatformDefaultWidth = false,
                                    decorFitsSystemWindows = false
                                )
                            ) {
                                AchievementsScreen(
                                    onBack = { showAchievements.value = false }
                                )
                            }
                        }


                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        checkStepChartIntent(intent)
    }

    private fun checkStepChartIntent(intent: Intent?) {
        if (intent?.action == StepCounterService.ACTION_SHOW_STEPS_CHART) {
            showHourlyStepsChart.value = true
        }
    }
}
