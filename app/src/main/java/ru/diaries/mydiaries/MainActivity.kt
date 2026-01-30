package ru.diaries.mydiaries

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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import ru.diaries.mydiaries.ui.history.HistoryScreen
import ru.diaries.mydiaries.ui.permissions.PermissionsScreen
import ru.diaries.mydiaries.ui.permissions.checkAllPermissionsGranted
import ru.diaries.mydiaries.ui.splash.SplashScreen
import ru.diaries.mydiaries.ui.theme.MyDiariesTheme
import ru.diaries.mydiaries.ui.timeline.TimelineScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var keepSplashOnScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before super.onCreate
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyDiariesTheme {
                var showSplash by rememberSaveable { mutableStateOf(true) }
                var showPermissions by rememberSaveable {
                    mutableStateOf(!checkAllPermissionsGranted(this@MainActivity))
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
                    if (showPermissions) {
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
                                NavigationBar {
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
                                            Text(text = stringResource(R.string.tab_timeline))
                                        }
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
                                            Text(text = stringResource(R.string.tab_history))
                                        }
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
                                    0 -> TimelineScreen(showFab = true)
                                    1 -> HistoryScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
