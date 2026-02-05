package ru.diaries.mydiaries.ui.statistics

sealed class StatisticsIntent {
    data object LoadData : StatisticsIntent()
    data class OpenChart(val type: StatisticsChartType) : StatisticsIntent()
    data object CloseChart : StatisticsIntent()
}
