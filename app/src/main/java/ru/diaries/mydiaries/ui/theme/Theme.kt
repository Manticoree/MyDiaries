package ru.diaries.mydiaries.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// === "Теплый Дневник" Light Color Scheme ===
private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = WarmBrown,
    onPrimary = OnWarmBrown,
    primaryContainer = Color(0xFFE8DED4),
    onPrimaryContainer = Color(0xFF3D2E1F),

    // Secondary colors
    secondary = SageGreen,
    onSecondary = OnSageGreen,
    secondaryContainer = Color(0xFFD4E8D4),
    onSecondaryContainer = Color(0xFF1F3D1F),

    // Tertiary colors
    tertiary = Terracotta,
    onTertiary = OnTerracotta,
    tertiaryContainer = Color(0xFFF5DDD0),
    onTertiaryContainer = Color(0xFF3D2116),

    // Background & Surface
    background = Parchment,
    onBackground = TextPrimary,
    surface = CreamWhite,
    onSurface = TextPrimary,
    surfaceVariant = SoftCream,
    onSurfaceVariant = TextSecondary,

    // Outline
    outline = Color(0xFFB8A899),
    outlineVariant = Color(0xFFD4C8BD),

    // Inverse
    inverseSurface = DarkSurface,
    inverseOnSurface = TextPrimaryDark,
    inversePrimary = WarmBrownDark,

    // Surface tones
    surfaceTint = WarmBrown,

    // Error colors (soft red that fits the warm palette)
    error = Color(0xFFB85450),
    onError = Color(0xFFFFFDF9),
    errorContainer = Color(0xFFF5D4D2),
    onErrorContainer = Color(0xFF3D1614)
)

// === "Теплый Дневник" Dark Color Scheme ===
private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = WarmBrownDark,
    onPrimary = OnWarmBrownDark,
    primaryContainer = Color(0xFF5C4A3A),
    onPrimaryContainer = Color(0xFFE8DED4),

    // Secondary colors
    secondary = SageGreenDark,
    onSecondary = OnSageGreenDark,
    secondaryContainer = Color(0xFF3A5C3A),
    onSecondaryContainer = Color(0xFFD4E8D4),

    // Tertiary colors
    tertiary = TerracottaDark,
    onTertiary = OnTerracottaDark,
    tertiaryContainer = Color(0xFF6B4A3A),
    onTertiaryContainer = Color(0xFFF5DDD0),

    // Background & Surface
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,

    // Outline
    outline = Color(0xFF6B5D52),
    outlineVariant = Color(0xFF4A4039),

    // Inverse
    inverseSurface = CreamWhite,
    inverseOnSurface = TextPrimary,
    inversePrimary = WarmBrown,

    // Surface tones
    surfaceTint = WarmBrownDark,

    // Error colors
    error = Color(0xFFE8A8A6),
    onError = Color(0xFF3D1614),
    errorContainer = Color(0xFF6B3230),
    onErrorContainer = Color(0xFFF5D4D2)
)

@Composable
fun MyDiariesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled to ensure our warm palette is always used
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Update system bars to match theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
