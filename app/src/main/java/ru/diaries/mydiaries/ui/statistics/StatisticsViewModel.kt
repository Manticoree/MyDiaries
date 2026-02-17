package ru.diaries.mydiaries.ui.statistics

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
import ru.diaries.mydiaries.data.repository.UserProfileRepository
import ru.diaries.mydiaries.domain.usecase.statistics.GetStatisticsDataUseCase
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getStatisticsDataUseCase: GetStatisticsDataUseCase,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state.asStateFlow()

    init {
        handleIntent(StatisticsIntent.LoadData)
    }

    fun handleIntent(intent: StatisticsIntent) {
        when (intent) {
            is StatisticsIntent.LoadData -> loadData()
            is StatisticsIntent.OpenChart -> openChart(intent.type)
            is StatisticsIntent.CloseChart -> closeChart()
            is StatisticsIntent.ShowAchievements -> showAchievements()
            is StatisticsIntent.HideAchievements -> hideAchievements()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getStatisticsDataUseCase(),
                userProfileRepository.getUserProfile()
            ) { data, profile ->
                val currentStreak = profile?.currentStreak ?: 0
                val longestStreak = profile?.longestStreak ?: 0
                StatisticsState(
                    isLoading = false,
                    statisticsData = data,
                    currentStreak = currentStreak,
                    longestStreak = longestStreak
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

    private fun openChart(type: StatisticsChartType) {
        _state.update { it.copy(openChart = type) }
    }

    private fun closeChart() {
        _state.update { it.copy(openChart = null) }
    }

    private fun showAchievements() {
        _state.update { it.copy(showAchievements = true) }
    }

    private fun hideAchievements() {
        _state.update { it.copy(showAchievements = false) }
    }
}
