package com.jerries.expense.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "accounts",
    indices = [Index(value = ["name"], unique = true)],
)
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    @ColumnInfo(name = "initial_balance_minor") val initialBalanceMinor: Long,
    @ColumnInfo(name = "currency_code") val currencyCode: String,
    @ColumnInfo(name = "color_argb") val colorArgb: Long,
    val archived: Boolean,
    @ColumnInfo(name = "created_at_epoch_millis") val createdAtEpochMillis: Long,
    @ColumnInfo(name = "updated_at_epoch_millis") val updatedAtEpochMillis: Long,
)
