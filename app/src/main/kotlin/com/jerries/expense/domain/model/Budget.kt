package com.jerries.expense.domain.model

data class Budget(
    val id: String,
    val categoryId: String,
    val limitMinor: Long,
    val startEpochDay: Long,
    val endEpochDay: Long,
)
