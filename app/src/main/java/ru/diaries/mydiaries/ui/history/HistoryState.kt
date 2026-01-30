package ru.diaries.mydiaries.ui.history

import ru.diaries.mydiaries.domain.usecase.history.DaySummary
import ru.diaries.mydiaries.domain.usecase.history.HistoryData

data class HistoryState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val historyData: HistoryData? = null,
    val selectedDay: DaySummary? = null,
    val showDayDetailDialog: Boolean = false
)
