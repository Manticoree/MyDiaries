package ru.diaries.mydiaries.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.diaries.mydiaries.data.local.dao.UserProfileDao
import ru.diaries.mydiaries.data.local.mapper.toEntity
import ru.diaries.mydiaries.data.local.mapper.toModel
import ru.diaries.mydiaries.data.model.UserProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao
) {
    fun getUserProfile(): Flow<UserProfile?> {
        return userProfileDao.getUserProfile().map { it?.toModel() }
    }

    suspend fun createProfile(userName: String) {
        val profile = UserProfile(
            id = 1,
            userName = userName,
            createdAt = System.currentTimeMillis(),
            totalSteps = 0,
            currentStreak = 0,
            longestStreak = 0
        )
        userProfileDao.insertProfile(profile.toEntity())
    }

    suspend fun updateUserName(userName: String) {
        userProfileDao.updateUserName(userName)
    }

    suspend fun addTotalSteps(steps: Long) {
        userProfileDao.addTotalSteps(steps)
    }

    suspend fun updateCurrentStreak(streak: Int) {
        userProfileDao.updateCurrentStreak(streak)
    }

    suspend fun updateLongestStreakIfGreater(streak: Int) {
        userProfileDao.updateLongestStreakIfGreater(streak)
    }
}
