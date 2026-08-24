package com.jerries.expense.domain.model

/** Direction of a transaction. Transfers are Phase 2 scope. */
enum class TransactionType { EXPENSE, INCOME, TRANSFER }

data class Transaction(
    val id: String,
    val accountId: String,
    val categoryId: String?,
    val amountMinor: Long,
    val type: TransactionType,
    val dateEpochDay: Long,
    val note: String?,
    val createdAtEpochMillis: Long,
) {
    val isIncome: Boolean get() = type == TransactionType.INCOME
}
