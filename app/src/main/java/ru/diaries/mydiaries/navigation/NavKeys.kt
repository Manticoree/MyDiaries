package ru.diaries.mydiaries.navigation

import java.util.UUID

sealed class NavKey {
    data object Home : NavKey()
    data class Editor(
        val entryId: String? = null,
        val newEntryKey: String = UUID.randomUUID().toString()
    ) : NavKey()
}
