package ru.diaries.mydiaries.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey
    val id: String,
    val uri: String,
    val thumbnailUri: String? = null,
    val durationMs: Long = 0L,
    val title: String? = null,
    val date: String, // ISO format: yyyy-MM-dd
    val createdAt: Long = System.currentTimeMillis()
)
