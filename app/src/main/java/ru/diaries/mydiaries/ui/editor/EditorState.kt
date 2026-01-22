package ru.diaries.mydiaries.ui.editor

import ru.diaries.mydiaries.data.model.Photo
import java.time.LocalDate

data class EditorState(
    val entryId: String? = null,
    val title: String = "",
    val content: String = "",
    val currentDate: LocalDate = LocalDate.now(),
    val photos: List<Photo> = emptyList(),
    val isSaving: Boolean = false,
    val isLoading: Boolean = false
) {
    companion object {
        const val MAX_PHOTOS = 6
    }

    val canAddMorePhotos: Boolean
        get() = photos.size < MAX_PHOTOS
}
