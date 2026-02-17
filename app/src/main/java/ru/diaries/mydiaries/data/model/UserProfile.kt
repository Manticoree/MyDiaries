package ru.diaries.mydiaries.data.model

data class UserProfile(
    val id: Int = 1,
    val userName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val totalSteps: Long = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
)
