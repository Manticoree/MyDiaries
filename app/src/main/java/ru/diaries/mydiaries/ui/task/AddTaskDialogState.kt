package ru.diaries.mydiaries.ui.task

data class AddTaskDialogState(
    val taskTitles: List<String> = listOf(""),
    val isSaving: Boolean = false,
    val error: String? = null
)

sealed class AddTaskDialogIntent {
    data class TitleChanged(val index: Int, val title: String) : AddTaskDialogIntent()
    data object AddField : AddTaskDialogIntent()
    data class RemoveField(val index: Int) : AddTaskDialogIntent()
    data object Save : AddTaskDialogIntent()
    data object ClearError : AddTaskDialogIntent()
}

sealed class AddTaskDialogEffect {
    data class SaveSuccess(val count: Int) : AddTaskDialogEffect()
}
