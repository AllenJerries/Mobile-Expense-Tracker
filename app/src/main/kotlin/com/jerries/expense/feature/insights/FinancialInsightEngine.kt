package com.jerries.expense.feature.insights

import com.jerries.expense.domain.model.Budget
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.DailyTotal
import com.jerries.expense.domain.model.SpendingByCategory
import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.usecase.BudgetSpending
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

data class Insight(
    val id: String,
    val message: String,
    val type: InsightType,
    val priority: Int = 0,
)

enum class InsightType { WARNING, POSITIVE, INFO }

interface InsightProvider {
    suspend fun generateInsights(context: InsightContext): List<Insight>
}

data class InsightContext(
    val currentMonth: YearMonth,
    val currentMonthExpenses: Long,
    val currentMonthIncome: Long,
    val lastMonthExpenses: Long,
    val lastMonthIncome: Long,
    val expensesByCategory: List<SpendingByCategory>,
    val budgetSpendings: List<BudgetSpending>,
    val dailyTotals: List<DailyTotal>,
    val allTransactions: List<Transaction>,
    val today: LocalDate,
)

@Singleton
class FinancialInsightEngine @Inject constructor() : InsightProvider {

    override suspend fun generateInsights(context: InsightContext): List<Insight> {
        if (context.currentMonthExpenses == 0L && context.currentMonthIncome == 0L) {
            return listOf(Insight("no_data", "Start tracking expenses to see insights.", InsightType.INFO))
        }
        val insights = mutableListOf<Insight>()
        insights.addAll(generateBudgetWarnings(context))
        insights.addAll(generateSpendingComparison(context))
        insights.addAll(generateHighestCategory(context))
        insights.addAll(generateSavingsInsight(context))
        insights.addAll(generateAvgDailyInsight(context))
        return insights.sortedByDescending { it.priority }
    }

    private fun generateBudgetWarnings(ctx: InsightContext): List<Insight> {
        return ctx.budgetSpendings.mapNotNull { bs ->
            if (bs.percentage > 0.8) {
                val catName = bs.budget.categoryId ?: "overall"
                Insight(
                    id = "budget_warn_${bs.budget.id}",
                    message = "If your current spending continues, you may exceed your $catName budget.",
                    type = if (bs.percentage > 1.0) InsightType.WARNING else InsightType.WARNING,
                    priority = if (bs.percentage > 1.0) 10 else 8,
                )
            } else null
        }
    }

    private fun generateSpendingComparison(ctx: InsightContext): List<Insight> {
        if (ctx.lastMonthExpenses == 0L) return emptyList()
        val changePercent = ((ctx.currentMonthExpenses - ctx.lastMonthExpenses).toFloat() / ctx.lastMonthExpenses.toFloat() * 100).toInt()
        return if (changePercent > 5) {
            listOf(Insight("spend_higher", "Your spending is ${kotlin.math.abs(changePercent)}% higher than last month.", InsightType.WARNING, priority = 7))
        } else if (changePercent < -5) {
            listOf(Insight("spend_lower", "Your spending is ${kotlin.math.abs(changePercent)}% lower than last month. Great job!", InsightType.POSITIVE, priority = 7))
        } else emptyList()
    }

    private fun generateHighestCategory(ctx: InsightContext): List<Insight> {
        val highest = ctx.expensesByCategory.maxByOrNull { it.totalMinor } ?: return emptyList()
        return listOf(Insight("highest_cat", "Your highest spending category is ${highest.categoryName}.", InsightType.INFO, priority = 5))
    }

    private fun generateSavingsInsight(ctx: InsightContext): List<Insight> {
        if (ctx.lastMonthIncome == 0L) return emptyList()
        val currentSavings = ctx.currentMonthIncome - ctx.currentMonthExpenses
        val lastSavings = ctx.lastMonthIncome - ctx.lastMonthExpenses
        return when {
            currentSavings > lastSavings && lastSavings > 0 -> listOf(Insight("savings_up", "You saved more this month than last month.", InsightType.POSITIVE, priority = 9))
            currentSavings < lastSavings && lastSavings > 0 -> listOf(Insight("savings_down", "Your savings decreased compared to last month.", InsightType.WARNING, priority = 6))
            else -> emptyList()
        }
    }

    private fun generateAvgDailyInsight(ctx: InsightContext): List<Insight> {
        val daysElapsed = ChronoUnit.DAYS.between(ctx.currentMonth.atDay(1), ctx.today) + 1
        if (daysElapsed <= 0) return emptyList()
        val avg = ctx.currentMonthExpenses / daysElapsed
        return listOf(Insight("avg_daily", "Your average daily spending is $${String.format("%.2f", avg / 100.0)}.", InsightType.INFO, priority = 4))
    }
}
