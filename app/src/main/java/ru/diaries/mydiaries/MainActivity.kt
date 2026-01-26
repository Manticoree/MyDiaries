package ru.diaries.mydiaries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import ru.diaries.mydiaries.ui.permissions.PermissionsScreen
import ru.diaries.mydiaries.ui.permissions.checkAllPermissionsGranted
import ru.diaries.mydiaries.ui.theme.MyDiariesTheme
import ru.diaries.mydiaries.ui.timeline.TimelineScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDiariesTheme {
                var showPermissions by rememberSaveable {
                    mutableStateOf(!checkAllPermissionsGranted(this@MainActivity))
                }

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
                    TimelineScreen()
                }
            }
        }
    }
}
