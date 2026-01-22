package ru.diaries.mydiaries.ui.editor

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.data.model.DiaryEntry
import ru.diaries.mydiaries.data.model.Photo
import ru.diaries.mydiaries.data.repository.DiaryRepository
import java.util.UUID
import javax.inject.Inject

sealed class EditorEffect {
    data object NavigateBack : EditorEffect()
}

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val repository: DiaryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<EditorEffect>()
    val effect = _effect.asSharedFlow()

    fun handleIntent(intent: EditorIntent) {
        when (intent) {
            is EditorIntent.LoadEntry -> loadEntry(intent.entryId)
            is EditorIntent.TitleChanged -> updateTitle(intent.title)
            is EditorIntent.ContentChanged -> updateContent(intent.content)
            is EditorIntent.AddPhotos -> addPhotos(intent.uris)
            is EditorIntent.RemovePhoto -> removePhoto(intent.photoId)
            is EditorIntent.SaveEntry -> saveEntry()
            is EditorIntent.NavigateBack -> navigateBack()
        }
    }

    private fun loadEntry(entryId: String?) {
        if (entryId == null) {
            _state.update {
                EditorState(
                    entryId = null,
                    title = "",
                    content = "",
                    currentDate = java.time.LocalDate.now(),
                    photos = emptyList(),
                    isSaving = false,
                    isLoading = false
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                EditorState(
                    entryId = null,
                    title = "",
                    content = "",
                    currentDate = java.time.LocalDate.now(),
                    photos = emptyList(),
                    isSaving = false,
                    isLoading = true
                )
            }
            val entry = repository.getEntry(entryId)
            if (entry != null) {
                _state.update {
                    EditorState(
                        entryId = entry.id,
                        title = entry.title,
                        content = entry.content,
                        currentDate = entry.date,
                        photos = entry.photos,
                        isSaving = false,
                        isLoading = false
                    )
                }
            } else {
                _state.update {
                    EditorState(
                        isLoading = false,
                        currentDate = java.time.LocalDate.now()
                    )
                }
            }
        }
    }

    private fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    private fun updateContent(content: String) {
        _state.update { it.copy(content = content) }
    }

    private fun addPhotos(uris: List<Uri>) {
        val currentState = _state.value
        val currentPhotos = currentState.photos.toMutableList()
        val availableSlots = EditorState.MAX_PHOTOS - currentPhotos.size

        if (availableSlots <= 0) return

        val photosToAdd = uris.take(availableSlots).mapIndexed { index, uri ->
            Photo(
                id = UUID.randomUUID().toString(),
                uri = uri.toString(),
                position = currentPhotos.size + index
            )
        }

        currentPhotos.addAll(photosToAdd)
        _state.update { it.copy(photos = currentPhotos) }
    }

    private fun removePhoto(photoId: String) {
        val currentPhotos = _state.value.photos.toMutableList()
        currentPhotos.removeAll { it.id == photoId }

        val updatedPhotos = currentPhotos.mapIndexed { index, photo ->
            photo.copy(position = index)
        }

        _state.update { it.copy(photos = updatedPhotos) }
    }

    private fun saveEntry() {
        val currentState = _state.value

        if (currentState.title.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val entry = DiaryEntry(
                id = currentState.entryId ?: UUID.randomUUID().toString(),
                title = currentState.title.trim(),
                content = currentState.content.trim(),
                date = currentState.currentDate,
                photos = currentState.photos
            )

            repository.saveEntry(entry)
            _effect.emit(EditorEffect.NavigateBack)
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _effect.emit(EditorEffect.NavigateBack)
        }
    }
}
