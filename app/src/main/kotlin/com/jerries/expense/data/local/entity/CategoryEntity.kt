package com.jerries.expense.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [Index(value = ["name", "kind"], unique = true)],
)
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val kind: String,
    @ColumnInfo(name = "icon_key") val iconKey: String?,
    @ColumnInfo(name = "color_argb") val colorArgb: Long,
)
