package ru.diaries.mydiaries.ui.achievements

import ru.diaries.mydiaries.data.local.entity.AchievementCategory

data class AchievementsState(
    val isLoading: Boolean = true,
    val allAchievements: List<AchievementItem> = emptyList(),
    val unlockedAchievements: List<AchievementItem> = emptyList(),
    val selectedCategory: AchievementCategory? = null,
    val showUnlockedOnly: Boolean = false,
    val error: String? = null
)

data class AchievementItem(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val category: AchievementCategory,
    val isUnlocked: Boolean,
    val progress: Float = 0f,
    val unlockedAt: Long? = null
)
