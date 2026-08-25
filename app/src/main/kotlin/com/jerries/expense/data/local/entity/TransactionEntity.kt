package com.jerries.expense.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("account_id"),
        Index("category_id"),
        Index("date_epoch_day"),
        Index("is_deleted"),
        Index("destination_account_id"),
    ],
)
data class TransactionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "account_id") val accountId: String,
    @ColumnInfo(name = "category_id") val categoryId: String?,
    @ColumnInfo(name = "amount_minor") val amountMinor: Long,
    val type: String,
    @ColumnInfo(name = "date_epoch_day") val dateEpochDay: Long,
    val title: String?,
    val note: String?,
    @ColumnInfo(name = "created_at_epoch_millis") val createdAtEpochMillis: Long,
    @ColumnInfo(name = "updated_at_epoch_millis") val updatedAtEpochMillis: Long,
    @ColumnInfo(name = "payment_method") val paymentMethod: String?,
    @ColumnInfo(name = "destination_account_id") val destinationAccountId: String?,
    @ColumnInfo(name = "recurring_transaction_id") val recurringTransactionId: String?,
    @ColumnInfo(name = "attachment_uri") val attachmentUri: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
)
