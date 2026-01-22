package ru.diaries.mydiaries.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.diaries.mydiaries.data.local.entity.ExpenseEntity

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY id")
    fun getExpensesByDate(date: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY id")
    suspend fun getExpensesByDateSync(date: Long): List<ExpenseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpense(id: String)

    @Query("SELECT SUM(amount) FROM expenses WHERE date = :date")
    suspend fun getTotalForDate(date: Long): Double?
}
