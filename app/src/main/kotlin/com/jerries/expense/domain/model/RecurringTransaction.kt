package com.jerries.expense.domain.model

enum class RecurrenceFrequency { DAILY, WEEKLY, BIWEEKLY, MONTHLY, QUARTERLY, YEARLY }

data class RecurringTransaction(
    val id: String,
    val type: TransactionType,
    val amountMinor: Long,
    val accountId: String,
    val categoryId: String?,
    val destinationAccountId: String?,
    val description: String?,
    val frequency: RecurrenceFrequency,
    val nextOccurrenceEpochDay: Long,
    val endDateEpochDay: Long?,
    val active: Boolean,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
