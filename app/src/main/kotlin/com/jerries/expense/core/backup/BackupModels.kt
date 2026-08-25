package com.jerries.expense.core.backup

import kotlinx.serialization.Serializable

object BackupSchema {
    const val SCHEMA_VERSION = 2
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
    val isDeleted: Boolean = false,
)

@Serializable
data class BackupPayload(
    val metadata: BackupMetadata,
    val accounts: List<BackupAccount> = emptyList(),
    val categories: List<BackupCategory> = emptyList(),
    val transactions: List<BackupTransaction> = emptyList(),
)
