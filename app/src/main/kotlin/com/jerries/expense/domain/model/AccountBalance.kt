package com.jerries.expense.domain.model

/** Computed running balance of an account in minor units. */
data class AccountBalance(
    val accountId: String,
    val balanceMinor: Long,
)
