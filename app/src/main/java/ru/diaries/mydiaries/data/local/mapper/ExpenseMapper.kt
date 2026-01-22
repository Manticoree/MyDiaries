package ru.diaries.mydiaries.data.local.mapper

import ru.diaries.mydiaries.data.local.entity.ExpenseEntity
import ru.diaries.mydiaries.data.model.Expense
import ru.diaries.mydiaries.data.model.ExpenseCategory
import java.time.LocalDate

fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = id,
        amount = amount,
        category = ExpenseCategory.valueOf(category),
        description = description,
        date = LocalDate.ofEpochDay(date)
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        amount = amount,
        category = category.name,
        description = description,
        date = date.toEpochDay()
    )
}

fun List<ExpenseEntity>.toDomainList(): List<Expense> {
    return map { it.toDomain() }
}
