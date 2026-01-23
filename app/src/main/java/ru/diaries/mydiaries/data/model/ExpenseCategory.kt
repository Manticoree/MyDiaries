package ru.diaries.mydiaries.data.model

import androidx.compose.ui.graphics.Color
import ru.diaries.mydiaries.ui.theme.DustyRose
import ru.diaries.mydiaries.ui.theme.GoldenHoney
import ru.diaries.mydiaries.ui.theme.InkBlue
import ru.diaries.mydiaries.ui.theme.LavenderMist
import ru.diaries.mydiaries.ui.theme.SageGreen
import ru.diaries.mydiaries.ui.theme.Terracotta
import ru.diaries.mydiaries.ui.theme.WarmBrown

enum class ExpenseCategory(
    val displayName: String,
    val color: Color
) {
    FOOD("Еда", SageGreen),                    // Шалфейный зелёный
    TRANSPORT("Транспорт", InkBlue),           // Чернильный синий
    ENTERTAINMENT("Развлечения", GoldenHoney), // Золотистый мёд
    SHOPPING("Покупки", DustyRose),            // Пыльная роза
    HEALTH("Здоровье", LavenderMist),          // Лавандовый
    UTILITIES("Коммунальные", WarmBrown),      // Тёплый коричневый
    OTHER("Другое", Terracotta)                // Терракотовый
}
