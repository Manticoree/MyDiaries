package ru.diaries.mydiaries.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val date: Long
)
