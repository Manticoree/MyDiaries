package ru.diaries.mydiaries.data.model

import java.time.LocalDate
import java.util.UUID

data class DiaryEntry(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val date: LocalDate = LocalDate.now(),
    val photos: List<Photo> = emptyList()
)
