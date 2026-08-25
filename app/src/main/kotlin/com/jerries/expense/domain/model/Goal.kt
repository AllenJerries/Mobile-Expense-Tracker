package com.jerries.expense.domain.model

data class SavingsGoal(
    val id: String,
    val name: String,
    val targetMinor: Long,
    val savedMinor: Long,
    val deadlineEpochDay: Long?,
    val icon: String?,
    val createdAtEpochMillis: Long,
    val completed: Boolean,
) {
    val progress: Double
        get() = if (targetMinor > 0) (savedMinor.toDouble() / targetMinor.toDouble()).coerceIn(0.0, 1.0) else 0.0
}
