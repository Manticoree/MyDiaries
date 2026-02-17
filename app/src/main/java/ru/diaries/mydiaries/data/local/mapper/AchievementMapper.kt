package ru.diaries.mydiaries.data.local.mapper

import ru.diaries.mydiaries.data.local.entity.AchievementEntity
import ru.diaries.mydiaries.data.model.Achievement

fun AchievementEntity.toModel() = Achievement(
    id = id,
    name = name,
    description = description,
    icon = icon,
    category = category,
    requiredSteps = requiredSteps,
    requiredStreak = requiredStreak,
    requiredDays = requiredDays,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt
)

fun Achievement.toEntity() = AchievementEntity(
    id = id,
    name = name,
    description = description,
    icon = icon,
    category = category,
    requiredSteps = requiredSteps,
    requiredStreak = requiredStreak,
    requiredDays = requiredDays,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt
)
