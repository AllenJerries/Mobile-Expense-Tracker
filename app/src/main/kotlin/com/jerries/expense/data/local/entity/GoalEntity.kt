package com.jerries.expense.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class SavingsGoalEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "target_minor") val targetMinor: Long,
    @ColumnInfo(name = "saved_minor") val savedMinor: Long,
    @ColumnInfo(name = "deadline_epoch_day") val deadlineEpochDay: Long?,
    val icon: String?,
    @ColumnInfo(name = "created_at_epoch_millis") val createdAtEpochMillis: Long,
    val completed: Boolean,
)
