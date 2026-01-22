package ru.diaries.mydiaries.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val category: String,
    val description: String,
    val date: Long
)
