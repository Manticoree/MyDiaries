package ru.diaries.mydiaries.domain.usecase

import kotlinx.coroutines.flow.first
import ru.diaries.mydiaries.data.repository.AchievementRepository
import ru.diaries.mydiaries.data.repository.UserProfileRepository
import ru.diaries.mydiaries.feature.track.data.repository.TrackRepository
import java.time.LocalDate
import javax.inject.Inject

class CheckAndUnlockAchievementsUseCase @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val userProfileRepository: UserProfileRepository,
    private val trackRepository: TrackRepository
) {
    suspend operator fun invoke() {
        val today = LocalDate.now()
        val todayTrack = trackRepository.getDailyTrack(today)
        val todaySteps = todayTrack?.steps ?: 0

        val profile = userProfileRepository.getUserProfile().first() ?: return

        // Calculate total steps from all tracks
        val allTracks = trackRepository.getAllTracks().first()
        val totalSteps = allTracks.sumOf { it.steps }.toLong()

        // Check and unlock achievements
        achievementRepository.checkAndUnlockAchievement(
            todaySteps = todaySteps,
            totalSteps = totalSteps,
            currentStreak = profile.currentStreak
        )

        // Update total steps in profile
        userProfileRepository.addTotalSteps(todaySteps.toLong())
    }
}
