package ru.diaries.mydiaries.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.diaries.mydiaries.data.local.dao.AchievementDao
import ru.diaries.mydiaries.data.local.entity.AchievementCategory
import ru.diaries.mydiaries.data.local.mapper.toEntity
import ru.diaries.mydiaries.data.local.mapper.toModel
import ru.diaries.mydiaries.data.model.Achievement
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepository @Inject constructor(
    private val achievementDao: AchievementDao
) {
    fun getAllAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAllAchievements().map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getUnlockedAchievements(): Flow<List<Achievement>> {
        return achievementDao.getUnlockedAchievements().map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getLockedAchievements(): Flow<List<Achievement>> {
        return achievementDao.getLockedAchievements().map { entities ->
            entities.map { it.toModel() }
        }
    }

    fun getAchievementsByCategory(category: AchievementCategory): Flow<List<Achievement>> {
        return achievementDao.getAchievementsByCategory(category).map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun initializeAchievements() {
        // Check if achievements already exist
        val existing = achievementDao.getAchievementById("first_steps_1000")
        if (existing != null) return

        val achievements = AchievementDefinitions.getAllAchievements()
        achievementDao.insertAchievements(achievements.map { it.toEntity() })
    }

    suspend fun checkAndUnlockAchievement(
        todaySteps: Int,
        totalSteps: Long,
        currentStreak: Int
    ): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()

        // Get all achievements as a list (not Flow)
        val achievements = achievementDao.getAllAchievements()
            .map { entities -> entities.map { it.toModel() } }
            .first()

        for (achievement in achievements) {
            if (achievement.isUnlocked) continue

            val shouldUnlock = when (achievement.category) {
                AchievementCategory.STEPS -> {
                    achievement.requiredSteps != null && todaySteps >= achievement.requiredSteps
                }
                AchievementCategory.TOTAL -> {
                    achievement.requiredSteps != null && totalSteps >= achievement.requiredSteps
                }
                AchievementCategory.STREAK -> {
                    achievement.requiredStreak != null && currentStreak >= achievement.requiredStreak
                }
                AchievementCategory.MILESTONE -> {
                    achievement.requiredSteps != null && totalSteps >= achievement.requiredSteps
                }
                else -> false
            }

            if (shouldUnlock) {
                achievementDao.unlockAchievement(achievement.id)
                newlyUnlocked.add(achievement)
            }
        }

        return newlyUnlocked
    }

    fun getUnlockedCount(): Flow<Int> = achievementDao.getUnlockedCount()
}
