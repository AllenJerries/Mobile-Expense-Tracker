package com.jerries.expense.domain.model

enum class BudgetPeriod { WEEKLY, MONTHLY, YEARLY, CUSTOM }

data class Budget(
    val id: String,
    val categoryId: String?,
    val accountId: String?,
    val limitMinor: Long,
    val period: BudgetPeriod,
    val startEpochDay: Long,
    val endEpochDay: Long,
    val alertThreshold: Double,
    val createdAtEpochMillis: Long,
)
