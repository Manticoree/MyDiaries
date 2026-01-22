package ru.diaries.mydiaries.ui.editor

import android.net.Uri

sealed class EditorIntent {
    data class LoadEntry(val entryId: String?) : EditorIntent()
    data class TitleChanged(val title: String) : EditorIntent()
    data class ContentChanged(val content: String) : EditorIntent()
    data class AddPhotos(val uris: List<Uri>) : EditorIntent()
    data class RemovePhoto(val photoId: String) : EditorIntent()
    data object SaveEntry : EditorIntent()
    data object NavigateBack : EditorIntent()
}
