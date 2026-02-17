package ru.diaries.mydiaries.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val userName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val totalSteps: Long = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
)
