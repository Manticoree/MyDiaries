package ru.diaries.mydiaries.domain.usecase.statistics

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.diaries.mydiaries.data.model.Expense
import ru.diaries.mydiaries.data.model.ExpenseCategory
import ru.diaries.mydiaries.data.repository.ExpenseRepository
import ru.diaries.mydiaries.feature.food.data.repository.FoodRepository
import ru.diaries.mydiaries.feature.track.data.repository.TrackRepository
import java.time.LocalDate
import javax.inject.Inject

data class DailyExpensePoint(
    val date: LocalDate,
    val total: Double
)

data class CategoryExpenseData(
    val category: ExpenseCategory,
    val total: Double,
    val percentage: Float
)

data class DailyStepsPoint(
    val date: LocalDate,
    val steps: Int
)

data class DailyCaloriesPoint(
    val date: LocalDate,
    val calories: Int
)

data class DailyDistancePoint(
    val date: LocalDate,
    val distanceKm: Double
)

data class StatisticsData(
    val expenses: List<Expense>,
    val dailyExpenses: List<DailyExpensePoint>,
    val categoryExpenses: List<CategoryExpenseData>,
    val totalExpenses: Double,
    val dailySteps: List<DailyStepsPoint>,
    val totalSteps: Int,
    val dailyCalories: List<DailyCaloriesPoint>,
    val totalCalories: Int,
    val dailyDistance: List<DailyDistancePoint>,
    val totalDistanceKm: Double
)

class GetStatisticsDataUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val foodRepository: FoodRepository,
    private val trackRepository: TrackRepository
) {
    operator fun invoke(): Flow<StatisticsData> {
        val thirtyDaysAgo = LocalDate.now().minusDays(30)

        return combine(
            expenseRepository.getAllExpenses(),
            foodRepository.getAllFoodEntries(),
            trackRepository.getAllTracks()
        ) { allExpenses, allFood, allTracks ->
            val expenses = allExpenses.filter { !it.date.isBefore(thirtyDaysAgo) }
            val food = allFood.filter { !it.date.isBefore(thirtyDaysAgo) }
            val tracks = allTracks.filter { !it.date.isBefore(thirtyDaysAgo) }

            val totalExpenses = expenses.sumOf { it.amount }
            val categoryExpenses = expenses
                .groupBy { it.category }
                .map { (category, items) ->
                    val total = items.sumOf { it.amount }
                    CategoryExpenseData(
                        category = category,
                        total = total,
                        percentage = if (totalExpenses > 0) (total / totalExpenses * 100).toFloat() else 0f
                    )
                }
                .sortedByDescending { it.total }

            val dailyExpenses = expenses
                .groupBy { it.date }
                .map { (date, items) -> DailyExpensePoint(date, items.sumOf { it.amount }) }
                .sortedBy { it.date }

            val dailySteps = tracks
                .map { DailyStepsPoint(it.date, it.steps) }
                .sortedBy { it.date }

            val dailyCalories = food
                .groupBy { it.date }
                .map { (date, items) -> DailyCaloriesPoint(date, items.sumOf { it.actualCalories }) }
                .sortedBy { it.date }

            val dailyDistance = tracks
                .map { DailyDistancePoint(it.date, it.distanceMeters / 1000.0) }
                .sortedBy { it.date }

            StatisticsData(
                expenses = expenses,
                dailyExpenses = dailyExpenses,
                categoryExpenses = categoryExpenses,
                totalExpenses = totalExpenses,
                dailySteps = dailySteps,
                totalSteps = tracks.sumOf { it.steps },
                dailyCalories = dailyCalories,
                totalCalories = food.sumOf { it.actualCalories },
                dailyDistance = dailyDistance,
                totalDistanceKm = tracks.sumOf { it.distanceMeters } / 1000.0
            )
        }
    }
}
