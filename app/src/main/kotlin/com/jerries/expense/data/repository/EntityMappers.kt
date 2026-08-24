package com.jerries.expense.data.repository

import com.jerries.expense.data.local.entity.AccountEntity
import com.jerries.expense.data.local.entity.BudgetEntity
import com.jerries.expense.data.local.entity.CategoryEntity
import com.jerries.expense.data.local.entity.GoalEntity
import com.jerries.expense.data.local.entity.TransactionEntity
import com.jerries.expense.domain.model.Account
import com.jerries.expense.domain.model.AccountType
import com.jerries.expense.domain.model.Budget
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.CategoryKind
import com.jerries.expense.domain.model.Goal
import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.model.TransactionType

internal fun AccountEntity.toDomain(): Account = Account(
    id = id,
    name = name,
    type = type.toAccountType(),
    initialBalanceMinor = initialBalanceMinor,
    currencyCode = currencyCode,
    colorArgb = colorArgb,
    archived = archived,
)

internal fun Account.toEntity(): AccountEntity = AccountEntity(
    id = id,
    name = name,
    type = type.name,
    initialBalanceMinor = initialBalanceMinor,
    currencyCode = currencyCode,
    colorArgb = colorArgb,
    archived = archived,
)

internal fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    kind = kind.toCategoryKind(),
    iconKey = iconKey,
    colorArgb = colorArgb,
)

internal fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    kind = kind.name,
    iconKey = iconKey,
    colorArgb = colorArgb,
)

internal fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    amountMinor = amountMinor,
    type = type.toTransactionType(),
    dateEpochDay = dateEpochDay,
    note = note,
    createdAtEpochMillis = createdAtEpochMillis,
)

internal fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    amountMinor = amountMinor,
    type = type.name,
    dateEpochDay = dateEpochDay,
    note = note,
    createdAtEpochMillis = createdAtEpochMillis,
)

internal fun BudgetEntity.toDomain(): Budget = Budget(
    id = id,
    categoryId = categoryId,
    limitMinor = limitMinor,
    startEpochDay = startEpochDay,
    endEpochDay = endEpochDay,
)

internal fun Budget.toEntity(): BudgetEntity = BudgetEntity(
    id = id,
    categoryId = categoryId,
    limitMinor = limitMinor,
    startEpochDay = startEpochDay,
    endEpochDay = endEpochDay,
)

internal fun GoalEntity.toDomain(): Goal = Goal(
    id = id,
    name = name,
    targetMinor = targetMinor,
    savedMinor = savedMinor,
    deadlineEpochDay = deadlineEpochDay,
)

internal fun Goal.toEntity(): GoalEntity = GoalEntity(
    id = id,
    name = name,
    targetMinor = targetMinor,
    savedMinor = savedMinor,
    deadlineEpochDay = deadlineEpochDay,
)

/** Tolerant enum parsing so corrupt rows never crash reads. */
private fun String.toAccountType(): AccountType =
    runCatching { AccountType.valueOf(this) }.getOrDefault(AccountType.OTHER)

private fun String.toCategoryKind(): CategoryKind =
    runCatching { CategoryKind.valueOf(this) }.getOrDefault(CategoryKind.EXPENSE)

private fun String.toTransactionType(): TransactionType =
    runCatching { TransactionType.valueOf(this) }.getOrDefault(TransactionType.EXPENSE)
