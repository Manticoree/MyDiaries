package ru.diaries.mydiaries.data.model

import java.time.LocalDate
import java.util.UUID

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val category: ExpenseCategory,
    val description: String = "",
    val date: LocalDate = LocalDate.now()
)
