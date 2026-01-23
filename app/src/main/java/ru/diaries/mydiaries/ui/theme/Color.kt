package ru.diaries.mydiaries.ui.theme

import androidx.compose.ui.graphics.Color

// === "Теплый Дневник" Color Palette ===

// Primary Colors - Light Theme
val WarmBrown = Color(0xFF8B7355)          // Primary - Тёплый коричневый
val SageGreen = Color(0xFF6B8E6B)          // Secondary - Шалфейный зелёный
val Terracotta = Color(0xFFCD7F5C)         // Tertiary - Терракотовый

// Background & Surface - Light Theme
val Parchment = Color(0xFFFFF8F0)          // Background - Пергаментный
val CreamWhite = Color(0xFFFFFDF9)         // Surface - Кремовый белый
val SoftCream = Color(0xFFFFF5E6)          // Surface Variant

// Accent Colors
val DustyRose = Color(0xFFCEA2AC)          // Пыльная роза
val GoldenHoney = Color(0xFFD4A84B)        // Золотистый мёд
val LavenderMist = Color(0xFF9B8AA8)       // Лавандовый туман
val InkBlue = Color(0xFF4A5D7A)            // Чернильный синий

// Text Colors - Light Theme
val TextPrimary = Color(0xFF3D3128)        // Primary text
val TextSecondary = Color(0xFF6B5D52)      // Secondary text
val TextTertiary = Color(0xFF8C7B6F)       // Tertiary text

// On Primary/Secondary/Tertiary
val OnWarmBrown = Color(0xFFFFFDF9)
val OnSageGreen = Color(0xFFFFFDF9)
val OnTerracotta = Color(0xFFFFFDF9)

// === Dark Theme Colors ===

// Primary Colors - Dark Theme
val WarmBrownDark = Color(0xFFD4C4B0)      // Lighter warm brown for dark theme
val SageGreenDark = Color(0xFFA8C4A8)      // Lighter sage for dark theme
val TerracottaDark = Color(0xFFE8A888)     // Lighter terracotta for dark theme

// Background & Surface - Dark Theme
val DarkBackground = Color(0xFF1A1614)     // Deep warm dark
val DarkSurface = Color(0xFF2D2621)        // Warm dark surface
val DarkSurfaceVariant = Color(0xFF3D352E) // Slightly lighter surface

// Text Colors - Dark Theme
val TextPrimaryDark = Color(0xFFF5EDE6)
val TextSecondaryDark = Color(0xFFD4C8BD)
val TextTertiaryDark = Color(0xFFB3A599)

// On Primary/Secondary/Tertiary - Dark Theme
val OnWarmBrownDark = Color(0xFF1A1614)
val OnSageGreenDark = Color(0xFF1A1614)
val OnTerracottaDark = Color(0xFF1A1614)

// Accent Colors - Dark Theme (brighter versions)
val DustyRoseDark = Color(0xFFE0BCC5)
val GoldenHoneyDark = Color(0xFFE8C06D)
val LavenderMistDark = Color(0xFFB8A6C4)
val InkBlueDark = Color(0xFF7A92B8)

// Category Colors (used for expense categories)
object CategoryColors {
    val Food = SageGreen                    // Еда → Шалфейный зелёный
    val Transport = InkBlue                 // Транспорт → Чернильный синий
    val Entertainment = GoldenHoney         // Развлечения → Золотистый мёд
    val Shopping = DustyRose                // Покупки → Пыльная роза
    val Health = LavenderMist               // Здоровье → Лавандовый
    val Utilities = WarmBrown               // Коммунальные → Тёплый коричневый
    val Other = Terracotta                  // Другое → Терракотовый
}

// Gradient combinations for decorative elements
object GradientColors {
    val WarmSunrise = listOf(Terracotta, GoldenHoney)
    val SoftMeadow = listOf(SageGreen, LavenderMist)
    val DustyEvening = listOf(DustyRose, LavenderMist)
    val InkyDepth = listOf(InkBlue, WarmBrown)
    val ParchmentGlow = listOf(Parchment, SoftCream)
}
