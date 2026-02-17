package ru.diaries.mydiaries.domain.usecase

import kotlinx.coroutines.flow.first
import ru.diaries.mydiaries.data.repository.AchievementRepository
import ru.diaries.mydiaries.data.repository.UserProfileRepository
import javax.inject.Inject

class InitializeAchievementsUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke() {
        // Initialize achievements in database
        achievementRepository.initializeAchievements()

        // Create default user profile if not exists
        val profile = userProfileRepository.getUserProfile().first()
        if (profile == null) {
            userProfileRepository.createProfile("Гость")
        }
    }
}
