package com.jerries.expense.domain.model

enum class CategoryKind { EXPENSE, INCOME }

/**
 * A spending/income category. [iconKey] and [colorArgb] are stored as plain
 * values so the data layer never depends on Compose types.
 */
data class Category(
    val id: String,
    val name: String,
    val kind: CategoryKind,
    val iconKey: String?,
    val colorArgb: Long,
)
