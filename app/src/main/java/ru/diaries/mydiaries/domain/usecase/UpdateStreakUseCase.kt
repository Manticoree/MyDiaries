package ru.diaries.mydiaries.domain.usecase

import kotlinx.coroutines.flow.first
import ru.diaries.mydiaries.data.model.UserProfile
import ru.diaries.mydiaries.data.repository.UserProfileRepository
import ru.diaries.mydiaries.feature.track.data.repository.TrackRepository
import java.time.LocalDate
import javax.inject.Inject

class UpdateStreakUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val trackRepository: TrackRepository
) {
    suspend operator fun invoke(todaySteps: Int, goal: Int = 10000) {
        val goalReached = todaySteps >= goal
        val userProfile = userProfileRepository.getUserProfile().first()

        if (userProfile == null) {
            // Create profile for new user
            userProfileRepository.createProfile("Гость")
            return
        }

        if (goalReached) {
            val newStreak = userProfile.currentStreak + 1
            userProfileRepository.updateCurrentStreak(newStreak)

            if (newStreak > userProfile.longestStreak) {
                userProfileRepository.updateLongestStreakIfGreater(newStreak)
            }
        } else {
            // Reset streak if goal not reached
            userProfileRepository.updateCurrentStreak(0)
        }
    }
}
