package ru.diaries.mydiaries.data.model

import java.util.UUID

data class Photo(
    val id: String = UUID.randomUUID().toString(),
    val uri: String,
    val position: Int
)
