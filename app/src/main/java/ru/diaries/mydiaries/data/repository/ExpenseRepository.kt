package ru.diaries.mydiaries.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.diaries.mydiaries.data.local.dao.ExpenseDao
import ru.diaries.mydiaries.data.local.mapper.toDomain
import ru.diaries.mydiaries.data.local.mapper.toDomainList
import ru.diaries.mydiaries.data.local.mapper.toEntity
import ru.diaries.mydiaries.data.model.Expense
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    fun getExpensesByDate(date: LocalDate): Flow<List<Expense>>
    suspend fun getExpensesByDateSync(date: LocalDate): List<Expense>
    suspend fun saveExpense(expense: Expense)
    suspend fun deleteExpense(id: String)
    suspend fun getTotalForDate(date: LocalDate): Double
}

@Singleton
class RoomExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses()
            .map { entities -> entities.toDomainList() }
    }

    override fun getExpensesByDate(date: LocalDate): Flow<List<Expense>> {
        return expenseDao.getExpensesByDate(date.toEpochDay())
            .map { entities -> entities.toDomainList() }
    }

    override suspend fun getExpensesByDateSync(date: LocalDate): List<Expense> {
        return expenseDao.getExpensesByDateSync(date.toEpochDay()).toDomainList()
    }

    override suspend fun saveExpense(expense: Expense) {
        expenseDao.insertExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(id: String) {
        expenseDao.deleteExpense(id)
    }

    override suspend fun getTotalForDate(date: LocalDate): Double {
        return expenseDao.getTotalForDate(date.toEpochDay()) ?: 0.0
    }
}
