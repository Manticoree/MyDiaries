package ru.diaries.mydiaries.ui.profile

import ru.diaries.mydiaries.data.model.Achievement
import ru.diaries.mydiaries.data.model.UserProfile

enum class AppTheme(val displayName: String) {
    SYSTEM("Системная"),
    LIGHT("Светлая"),
    DARK("Тёмная")
}

data class ProfileState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile? = null,
    val unlockedAchievements: List<Achievement> = emptyList(),
    val isEditingName: Boolean = false,
    val newUserName: String = "",
    val error: String? = null,
    val selectedTheme: AppTheme = AppTheme.SYSTEM,
    val showThemeDialog: Boolean = false
)
