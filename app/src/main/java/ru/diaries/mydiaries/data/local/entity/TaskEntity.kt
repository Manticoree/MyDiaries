package ru.diaries.mydiaries.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val date: String, // ISO format: yyyy-MM-dd
    val createdAt: Long = System.currentTimeMillis()
)
