package ru.diaries.mydiaries.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.data.model.UserProfile
import ru.diaries.mydiaries.data.PreferencesManager
import ru.diaries.mydiaries.data.repository.AchievementRepository
import ru.diaries.mydiaries.data.repository.UserProfileRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val achievementRepository: AchievementRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    init {
        loadTheme()
        handleIntent(ProfileIntent.LoadData)
    }

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadData -> loadData()
            is ProfileIntent.StartEditingName -> startEditingName()
            is ProfileIntent.CancelEditingName -> cancelEditingName()
            is ProfileIntent.UpdateName -> updateName(intent.name)
            is ProfileIntent.SaveName -> saveName()
            is ProfileIntent.ShowProfile -> _state.update { it.copy(isEditingName = false) }
            is ProfileIntent.ShowThemeDialog -> showThemeDialog()
            is ProfileIntent.HideThemeDialog -> hideThemeDialog()
            is ProfileIntent.SetTheme -> setTheme(intent.theme)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            userProfileRepository.getUserProfile()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { profile ->
                    if (profile == null) {
                        _state.update { it.copy(isLoading = false, userProfile = null) }
                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                userProfile = profile,
                                newUserName = profile.userName
                            )
                        }
                    }
                }
        }

        viewModelScope.launch {
            achievementRepository.getUnlockedAchievements()
                .catch { e ->
                    _state.update { it.copy(error = e.message) }
                }
                .collect { achievements ->
                    _state.update { it.copy(unlockedAchievements = achievements) }
                }
        }
    }

    private fun startEditingName() {
        val currentName = _state.value.userProfile?.userName ?: ""
        _state.update { it.copy(isEditingName = true, newUserName = currentName) }
    }

    private fun cancelEditingName() {
        val currentName = _state.value.userProfile?.userName ?: ""
        _state.update { it.copy(isEditingName = false, newUserName = currentName) }
    }

    private fun updateName(name: String) {
        _state.update { it.copy(newUserName = name) }
    }

    private fun saveName() {
        viewModelScope.launch {
            try {
                val newName = _state.value.newUserName.trim()
                if (newName.isNotEmpty()) {
                    val profile = _state.value.userProfile
                    if (profile != null) {
                        userProfileRepository.updateUserName(newName)
                        _state.update {
                            it.copy(
                                userProfile = profile.copy(userName = newName),
                                isEditingName = false
                            )
                        }
                    } else {
                        userProfileRepository.createProfile(newName)
                        _state.update {
                            it.copy(
                                userProfile = UserProfile(userName = newName),
                                isEditingName = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun loadTheme() {
        _state.update { it.copy(selectedTheme = preferencesManager.appTheme) }
    }

    private fun showThemeDialog() {
        _state.update { it.copy(showThemeDialog = true) }
    }

    private fun hideThemeDialog() {
        _state.update { it.copy(showThemeDialog = false) }
    }

    private fun setTheme(theme: AppTheme) {
        preferencesManager.appTheme = theme
        _state.update { it.copy(selectedTheme = theme, showThemeDialog = false) }
    }
}
