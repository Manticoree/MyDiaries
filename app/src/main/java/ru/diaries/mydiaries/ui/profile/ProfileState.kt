package ru.diaries.mydiaries.ui.profile

import ru.diaries.mydiaries.data.model.Achievement
import ru.diaries.mydiaries.data.model.UserProfile

data class ProfileState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile? = null,
    val unlockedAchievements: List<Achievement> = emptyList(),
    val isEditingName: Boolean = false,
    val newUserName: String = "",
    val error: String? = null
)
