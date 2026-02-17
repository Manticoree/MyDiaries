package ru.diaries.mydiaries.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val icon: String, // Emoji as icon
    val category: AchievementCategory,
    val requiredSteps: Int? = null,
    val requiredStreak: Int? = null,
    val requiredDays: Int? = null,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

enum class AchievementCategory {
    STEPS,
    STREAK,
    MILESTONE,
    SPECIAL,
    DAILY,
    WEEKLY,
    MONTHLY,
    TOTAL
}
