package ru.diaries.mydiaries.feature.video.data.model

import java.time.LocalDate

data class Video(
    val id: String,
    val uri: String,
    val thumbnailUri: String? = null,
    val durationMs: Long = 0L,
    val title: String? = null,
    val date: LocalDate = LocalDate.now(),
    val createdAt: Long = System.currentTimeMillis()
)
