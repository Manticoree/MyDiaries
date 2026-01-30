package ru.diaries.mydiaries.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.domain.usecase.history.GetHistoryDataUseCase
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryDataUseCase: GetHistoryDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    init {
        handleIntent(HistoryIntent.LoadHistory)
    }

    fun handleIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.LoadHistory -> loadHistory()
            is HistoryIntent.DayClicked -> openDayDetail(intent.day)
            is HistoryIntent.CloseDayDetail -> closeDayDetail()
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            getHistoryDataUseCase()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { data ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            historyData = data
                        )
                    }
                }
        }
    }

    private fun openDayDetail(day: ru.diaries.mydiaries.domain.usecase.history.DaySummary) {
        _state.update { it.copy(selectedDay = day, showDayDetailDialog = true) }
    }

    private fun closeDayDetail() {
        _state.update { it.copy(selectedDay = null, showDayDetailDialog = false) }
    }
}
