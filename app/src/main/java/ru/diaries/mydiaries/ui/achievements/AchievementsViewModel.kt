package ru.diaries.mydiaries.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.data.local.entity.AchievementCategory
import ru.diaries.mydiaries.data.repository.AchievementRepository
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AchievementsState())
    val state: StateFlow<AchievementsState> = _state.asStateFlow()

    init {
        handleIntent(AchievementsIntent.LoadData)
    }

    fun handleIntent(intent: AchievementsIntent) {
        when (intent) {
            is AchievementsIntent.LoadData -> loadData()
            is AchievementsIntent.FilterByCategory -> filterByCategory(intent.category)
            is AchievementsIntent.ToggleUnlockedOnly -> toggleUnlockedOnly(intent.showOnly)
            is AchievementsIntent.Back -> _state.update { it.copy(selectedCategory = null) }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                achievementRepository.getAllAchievements(),
                achievementRepository.getUnlockedAchievements()
            ) { all, unlocked ->
                val allItems = all.map { it.toAchievementItem(unlocked) }
                val unlockedItems = unlocked.map { it.toAchievementItem(unlocked) }
                AchievementsState(
                    isLoading = false,
                    allAchievements = allItems,
                    unlockedAchievements = unlockedItems
                )
            }
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { newState ->
                    _state.update { newState }
                }
        }
    }

    private fun filterByCategory(category: AchievementCategory?) {
        _state.update { it.copy(selectedCategory = category) }
    }

    private fun toggleUnlockedOnly(showOnly: Boolean) {
        _state.update { it.copy(showUnlockedOnly = showOnly) }
    }

    private fun ru.diaries.mydiaries.data.model.Achievement.toAchievementItem(
        unlocked: List<ru.diaries.mydiaries.data.model.Achievement>
    ): AchievementItem {
        val isUnlocked = unlocked.any { it.id == this.id }
        val unlockedAt = unlocked.find { it.id == this.id }?.unlockedAt

        val progress = if (isUnlocked) 1f else 0f

        return AchievementItem(
            id = id,
            name = name,
            description = description,
            icon = icon,
            category = category,
            isUnlocked = isUnlocked,
            progress = progress,
            unlockedAt = unlockedAt
        )
    }
}
