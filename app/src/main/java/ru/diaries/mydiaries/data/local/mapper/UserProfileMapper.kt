package ru.diaries.mydiaries.data.local.mapper

import ru.diaries.mydiaries.data.local.entity.UserProfileEntity
import ru.diaries.mydiaries.data.model.UserProfile

fun UserProfileEntity.toModel() = UserProfile(
    id = id,
    userName = userName,
    createdAt = createdAt,
    totalSteps = totalSteps,
    currentStreak = currentStreak,
    longestStreak = longestStreak
)

fun UserProfile.toEntity() = UserProfileEntity(
    id = id,
    userName = userName,
    createdAt = createdAt,
    totalSteps = totalSteps,
    currentStreak = currentStreak,
    longestStreak = longestStreak
)
