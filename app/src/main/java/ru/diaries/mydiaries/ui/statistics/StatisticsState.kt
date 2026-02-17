package ru.diaries.mydiaries.ui.statistics

import ru.diaries.mydiaries.domain.usecase.statistics.StatisticsData

enum class StatisticsChartType {
    EXPENSES, STEPS, CALORIES, DISTANCE
}

data class StatisticsState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val statisticsData: StatisticsData? = null,
    val openChart: StatisticsChartType? = null,
    val showAchievements: Boolean = false,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
)
