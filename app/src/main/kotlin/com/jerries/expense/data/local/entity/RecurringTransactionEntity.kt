package com.jerries.expense.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recurring_transactions",
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
        Index("next_occurrence_epoch_day"),
        Index("active"),
    ],
)
data class RecurringTransactionEntity(
    @PrimaryKey val id: String,
    val type: String,
    @ColumnInfo(name = "amount_minor") val amountMinor: Long,
    @ColumnInfo(name = "account_id") val accountId: String,
    @ColumnInfo(name = "category_id") val categoryId: String?,
    @ColumnInfo(name = "destination_account_id") val destinationAccountId: String?,
    val description: String?,
    val frequency: String,
    @ColumnInfo(name = "next_occurrence_epoch_day") val nextOccurrenceEpochDay: Long,
    @ColumnInfo(name = "end_date_epoch_day") val endDateEpochDay: Long?,
    val active: Boolean,
    @ColumnInfo(name = "created_at_epoch_millis") val createdAtEpochMillis: Long,
    @ColumnInfo(name = "updated_at_epoch_millis") val updatedAtEpochMillis: Long,
)
