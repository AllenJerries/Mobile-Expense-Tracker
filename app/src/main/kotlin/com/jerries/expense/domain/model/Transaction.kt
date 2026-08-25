package com.jerries.expense.domain.model

enum class TransactionType { EXPENSE, INCOME, TRANSFER }

data class Transaction(
    val id: String,
    val accountId: String,
    val categoryId: String?,
    val amountMinor: Long,
    val type: TransactionType,
    val dateEpochDay: Long,
    val title: String?,
    val note: String?,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val paymentMethod: String?,
    val destinationAccountId: String?,
    val recurringTransactionId: String?,
    val attachmentUri: String?,
    val isDeleted: Boolean,
) {
    val isIncome: Boolean get() = type == TransactionType.INCOME
    val isExpense: Boolean get() = type == TransactionType.EXPENSE
    val isTransfer: Boolean get() = type == TransactionType.TRANSFER
}
