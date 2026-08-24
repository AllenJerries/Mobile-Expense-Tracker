package com.jerries.expense.domain.model

data class Goal(
    val id: String,
    val name: String,
    val targetMinor: Long,
    val savedMinor: Long,
    val deadlineEpochDay: Long?,
)
