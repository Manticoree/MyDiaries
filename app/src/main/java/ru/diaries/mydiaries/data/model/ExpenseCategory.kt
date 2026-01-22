package ru.diaries.mydiaries.data.model

import androidx.compose.ui.graphics.Color

enum class ExpenseCategory(
    val displayName: String,
    val color: Color
) {
    FOOD("Еда", Color(0xFF4CAF50)),
    TRANSPORT("Транспорт", Color(0xFF2196F3)),
    ENTERTAINMENT("Развлечения", Color(0xFFFF9800)),
    SHOPPING("Покупки", Color(0xFFE91E63)),
    HEALTH("Здоровье", Color(0xFF9C27B0)),
    UTILITIES("Коммунальные", Color(0xFF607D8B)),
    OTHER("Другое", Color(0xFF795548))
}
