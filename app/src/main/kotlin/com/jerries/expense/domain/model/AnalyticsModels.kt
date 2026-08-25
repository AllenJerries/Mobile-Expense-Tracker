package com.jerries.expense.domain.model

data class DailyTotal(
    val dateEpochDay: Long,
    val expenseMinor: Long,
    val incomeMinor: Long,
)

data class MonthlyTotal(
    val yearMonth: String,
    val expenseMinor: Long,
    val incomeMinor: Long,
)

data class SpendingByCategory(
    val categoryId: String,
    val categoryName: String,
    val totalMinor: Long,
    val colorArgb: Long,
)

data class SpendingTrend(
    val dateEpochDay: Long,
    val amountMinor: Long,
)
