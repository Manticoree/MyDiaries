package ru.diaries.mydiaries.data.model

import ru.diaries.mydiaries.data.local.entity.AchievementCategory

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val category: AchievementCategory,
    val requiredSteps: Int? = null,
    val requiredStreak: Int? = null,
    val requiredDays: Int? = null,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)
