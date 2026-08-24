package com.jerries.expense.domain.model

enum class AccountType { CASH, BANK, CARD, WALLET, SAVINGS, OTHER }

data class Account(
    val id: String,
    val name: String,
    val type: AccountType,
    val initialBalanceMinor: Long,
    val currencyCode: String,
    val colorArgb: Long,
    val archived: Boolean,
)
