package ru.diaries.mydiaries.ui.history

import ru.diaries.mydiaries.domain.usecase.history.DaySummary

sealed class HistoryIntent {
    data object LoadHistory : HistoryIntent()
    data class DayClicked(val day: DaySummary) : HistoryIntent()
    data object CloseDayDetail : HistoryIntent()
}
