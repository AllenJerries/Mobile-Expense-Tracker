package com.jerries.expense.core.backup

import kotlinx.serialization.Serializable

object BackupSchema {
    const val SCHEMA_VERSION = 3
    const val FILE_EXTENSION = "jebak"
    const val APP_ID = "com.jerries.expense"
}

@Serializable
data class BackupMetadata(
    val schemaVersion: Int = BackupSchema.SCHEMA_VERSION,
    val appId: String = BackupSchema.APP_ID,
    val exportedAtEpochMillis: Long,
    val transactionCount: Int,
    val accountCount: Int,
    val categoryCount: Int,
    val budgetCount: Int = 0,
    val goalCount: Int = 0,
    val recurringCount: Int = 0,
)

@Serializable
data class BackupAccount(
    val id: String,
    val name: String,
    val type: String,
    val initialBalanceMinor: Long,
    val currencyCode: String,
    val colorArgb: Long,
    val archived: Boolean,
    val createdAtEpochMillis: Long = 0L,
    val updatedAtEpochMillis: Long = 0L,
)

@Serializable
data class BackupCategory(
    val id: String,
    val name: String,
    val kind: String,
    val iconKey: String? = null,
    val colorArgb: Long,
    val isDefault: Boolean = false,
    val isArchived: Boolean = false,
)

@Serializable
data class BackupTransaction(
    val id: String,
    val accountId: String,
    val categoryId: String? = null,
    val amountMinor: Long,
    val type: String,
    val dateEpochDay: Long,
    val title: String? = null,
    val note: String? = null,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long = 0L,
    val paymentMethod: String? = null,
    val destinationAccountId: String? = null,
    val recurringTransactionId: String? = null,
    val isDeleted: Boolean = false,
)

@Serializable
data class BackupBudget(
    val id: String,
    val categoryId: String? = null,
    val accountId: String? = null,
    val limitMinor: Long,
    val period: String,
    val startEpochDay: Long,
    val endEpochDay: Long,
    val alertThreshold: Double,
    val createdAtEpochMillis: Long = 0L,
)

@Serializable
data class BackupGoal(
    val id: String,
    val name: String,
    val targetMinor: Long,
    val savedMinor: Long,
    val deadlineEpochDay: Long? = null,
    val icon: String? = null,
    val createdAtEpochMillis: Long = 0L,
    val completed: Boolean = false,
)

@Serializable
data class BackupRecurringTransaction(
    val id: String,
    val type: String,
    val amountMinor: Long,
    val accountId: String,
    val categoryId: String? = null,
    val destinationAccountId: String? = null,
    val description: String? = null,
    val frequency: String,
    val nextOccurrenceEpochDay: Long,
    val endDateEpochDay: Long? = null,
    val active: Boolean = true,
    val createdAtEpochMillis: Long = 0L,
    val updatedAtEpochMillis: Long = 0L,
)

@Serializable
data class BackupPayload(
    val metadata: BackupMetadata,
    val accounts: List<BackupAccount> = emptyList(),
    val categories: List<BackupCategory> = emptyList(),
    val transactions: List<BackupTransaction> = emptyList(),
    val budgets: List<BackupBudget> = emptyList(),
    val goals: List<BackupGoal> = emptyList(),
    val recurringTransactions: List<BackupRecurringTransaction> = emptyList(),
)
