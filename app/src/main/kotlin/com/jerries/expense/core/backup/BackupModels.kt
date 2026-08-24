package com.jerries.expense.core.backup

import kotlinx.serialization.Serializable

/**
 * Schema for portable backup files. The file is a single JSON document
 * produced with kotlinx.serialization; bump [SCHEMA_VERSION] whenever the
 * format changes so future migrations can detect old exports.
 */
object BackupSchema {
    const val SCHEMA_VERSION = 1
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
)

@Serializable
data class BackupCategory(
    val id: String,
    val name: String,
    val kind: String,
    val iconKey: String? = null,
    val colorArgb: Long,
)

@Serializable
data class BackupTransaction(
    val id: String,
    val accountId: String,
    val categoryId: String? = null,
    val amountMinor: Long,
    val type: String,
    val dateEpochDay: Long,
    val note: String? = null,
    val createdAtEpochMillis: Long,
)

@Serializable
data class BackupPayload(
    val metadata: BackupMetadata,
    val accounts: List<BackupAccount> = emptyList(),
    val categories: List<BackupCategory> = emptyList(),
    val transactions: List<BackupTransaction> = emptyList(),
)
