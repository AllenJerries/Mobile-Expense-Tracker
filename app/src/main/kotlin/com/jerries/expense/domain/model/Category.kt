package com.jerries.expense.domain.model

enum class CategoryKind { EXPENSE, INCOME }

data class Category(
    val id: String,
    val name: String,
    val kind: CategoryKind,
    val iconKey: String?,
    val colorArgb: Long,
    val isDefault: Boolean,
    val isArchived: Boolean,
)
