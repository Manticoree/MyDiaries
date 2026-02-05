package ru.diaries.mydiaries.feature.video.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.feature.video.domain.usecase.SaveVideoUseCase
import ru.diaries.mydiaries.feature.video.data.model.Video
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddVideoDialogViewModel @Inject constructor(
    private val saveVideoUseCase: SaveVideoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddVideoDialogState())
    val state: StateFlow<AddVideoDialogState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AddVideoDialogEffect>()
    val effect: SharedFlow<AddVideoDialogEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: AddVideoDialogIntent) {
        when (intent) {
            is AddVideoDialogIntent.VideoSelected -> selectVideo(intent.uri, intent.durationMs, intent.thumbnailUri)
            is AddVideoDialogIntent.TitleChanged -> updateTitle(intent.title)
            is AddVideoDialogIntent.Save -> saveVideo()
            is AddVideoDialogIntent.ClearError -> clearError()
            is AddVideoDialogIntent.Clear -> clearState()
        }
    }

    private fun selectVideo(uri: Uri, durationMs: Long, thumbnailUri: String?) {
        _state.update {
            it.copy(
                selectedVideoUri = uri,
                videoDurationMs = durationMs,
                thumbnailUri = thumbnailUri,
                error = null
            )
        }
    }

    private fun updateTitle(title: String) {
        _state.update { it.copy(videoTitle = title, error = null) }
    }

    private fun saveVideo() {
        val uri = _state.value.selectedVideoUri

        if (uri == null) {
            _state.update { it.copy(error = "Please select a video") }
            return
        }

        _state.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            try {
                val video = Video(
                    id = UUID.randomUUID().toString(),
                    uri = uri.toString(),
                    thumbnailUri = _state.value.thumbnailUri,
                    durationMs = _state.value.videoDurationMs,
                    title = _state.value.videoTitle.takeIf { it.isNotBlank() }
                )
                saveVideoUseCase(video)
                _state.update { it.copy(isSaving = false) }
                _effect.emit(AddVideoDialogEffect.SaveSuccess)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Failed to save video"
                    )
                }
            }
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun clearState() {
        _state.value = AddVideoDialogState()
    }
}
