package com.jerries.expense.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    indices = [
        Index("category_id"),
        Index("account_id"),
    ],
)
data class BudgetEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "category_id") val categoryId: String?,
    @ColumnInfo(name = "account_id") val accountId: String?,
    @ColumnInfo(name = "limit_minor") val limitMinor: Long,
    val period: String,
    @ColumnInfo(name = "start_epoch_day") val startEpochDay: Long,
    @ColumnInfo(name = "end_epoch_day") val endEpochDay: Long,
    @ColumnInfo(name = "alert_threshold") val alertThreshold: Double,
    @ColumnInfo(name = "created_at_epoch_millis") val createdAtEpochMillis: Long,
)
