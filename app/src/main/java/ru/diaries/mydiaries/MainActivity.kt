package ru.diaries.mydiaries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import ru.diaries.mydiaries.ui.home.HomeScreen
import ru.diaries.mydiaries.ui.theme.MyDiariesTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDiariesTheme {
                HomeScreen()
            }
        }
    }
}
