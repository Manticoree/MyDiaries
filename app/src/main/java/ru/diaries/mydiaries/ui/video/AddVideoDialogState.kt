package ru.diaries.mydiaries.ui.video

import android.net.Uri

data class AddVideoDialogState(
    val selectedVideoUri: Uri? = null,
    val videoTitle: String = "",
    val videoDurationMs: Long = 0L,
    val thumbnailUri: String? = null,
    val isSaving: Boolean = false,
    val error: String? = null
)

sealed class AddVideoDialogIntent {
    data class VideoSelected(val uri: Uri, val durationMs: Long, val thumbnailUri: String?) : AddVideoDialogIntent()
    data class TitleChanged(val title: String) : AddVideoDialogIntent()
    data object Save : AddVideoDialogIntent()
    data object ClearError : AddVideoDialogIntent()
    data object Clear : AddVideoDialogIntent()
}

sealed class AddVideoDialogEffect {
    data object SaveSuccess : AddVideoDialogEffect()
}
