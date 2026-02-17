package ru.diaries.mydiaries.ui.achievements

import ru.diaries.mydiaries.data.local.entity.AchievementCategory

sealed class AchievementsIntent {
    data object LoadData : AchievementsIntent()
    data class FilterByCategory(val category: AchievementCategory?) : AchievementsIntent()
    data class ToggleUnlockedOnly(val showOnly: Boolean) : AchievementsIntent()
    data object Back : AchievementsIntent()
}
