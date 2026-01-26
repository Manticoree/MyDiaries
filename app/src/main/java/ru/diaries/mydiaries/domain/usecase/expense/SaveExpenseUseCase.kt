package ru.diaries.mydiaries.domain.usecase.expense

import ru.diaries.mydiaries.data.model.Expense
import ru.diaries.mydiaries.data.model.ExpenseCategory
import ru.diaries.mydiaries.data.repository.ExpenseRepository
import java.time.LocalDate
import javax.inject.Inject

class SaveExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(
        amount: Double,
        category: ExpenseCategory,
        description: String,
        date: LocalDate = LocalDate.now()
    ): Result<Unit> {
        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than 0"))
        }

        return try {
            val expense = Expense(
                amount = amount,
                category = category,
                description = description.trim(),
                date = date
            )
            expenseRepository.saveExpense(expense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
