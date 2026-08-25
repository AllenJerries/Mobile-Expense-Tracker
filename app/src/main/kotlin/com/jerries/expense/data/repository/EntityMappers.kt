package com.jerries.expense.data.repository

import com.jerries.expense.data.local.dao.DailyTotalProjection
import com.jerries.expense.data.local.dao.SpendingByCategoryProjection
import com.jerries.expense.data.local.entity.AccountEntity
import com.jerries.expense.data.local.entity.BudgetEntity
import com.jerries.expense.data.local.entity.CategoryEntity
import com.jerries.expense.data.local.entity.RecurringTransactionEntity
import com.jerries.expense.data.local.entity.SavingsGoalEntity
import com.jerries.expense.data.local.entity.TransactionEntity
import com.jerries.expense.domain.model.Account
import com.jerries.expense.domain.model.AccountType
import com.jerries.expense.domain.model.Budget
import com.jerries.expense.domain.model.BudgetPeriod
import com.jerries.expense.domain.model.Category
import com.jerries.expense.domain.model.CategoryKind
import com.jerries.expense.domain.model.DailyTotal
import com.jerries.expense.domain.model.RecurringTransaction
import com.jerries.expense.domain.model.RecurrenceFrequency
import com.jerries.expense.domain.model.SavingsGoal
import com.jerries.expense.domain.model.SpendingByCategory
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
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun Account.toEntity(): AccountEntity = AccountEntity(
    id = id,
    name = name,
    type = type.name,
    initialBalanceMinor = initialBalanceMinor,
    currencyCode = currencyCode,
    colorArgb = colorArgb,
    archived = archived,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    kind = kind.toCategoryKind(),
    iconKey = iconKey,
    colorArgb = colorArgb,
    isDefault = isDefault,
    isArchived = isArchived,
)

internal fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    kind = kind.name,
    iconKey = iconKey,
    colorArgb = colorArgb,
    isDefault = isDefault,
    isArchived = isArchived,
)

internal fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    amountMinor = amountMinor,
    type = type.toTransactionType(),
    dateEpochDay = dateEpochDay,
    title = title,
    note = note,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
    paymentMethod = paymentMethod,
    destinationAccountId = destinationAccountId,
    recurringTransactionId = recurringTransactionId,
    attachmentUri = attachmentUri,
    isDeleted = isDeleted,
)

internal fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    amountMinor = amountMinor,
    type = type.name,
    dateEpochDay = dateEpochDay,
    title = title,
    note = note,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
    paymentMethod = paymentMethod,
    destinationAccountId = destinationAccountId,
    recurringTransactionId = recurringTransactionId,
    attachmentUri = attachmentUri,
    isDeleted = isDeleted,
)

internal fun BudgetEntity.toDomain(): Budget = Budget(
    id = id,
    categoryId = categoryId,
    accountId = accountId,
    limitMinor = limitMinor,
    period = period.toBudgetPeriod(),
    startEpochDay = startEpochDay,
    endEpochDay = endEpochDay,
    alertThreshold = alertThreshold,
    createdAtEpochMillis = createdAtEpochMillis,
)

internal fun Budget.toEntity(): BudgetEntity = BudgetEntity(
    id = id,
    categoryId = categoryId,
    accountId = accountId,
    limitMinor = limitMinor,
    period = period.name,
    startEpochDay = startEpochDay,
    endEpochDay = endEpochDay,
    alertThreshold = alertThreshold,
    createdAtEpochMillis = createdAtEpochMillis,
)

internal fun SavingsGoalEntity.toDomain(): SavingsGoal = SavingsGoal(
    id = id,
    name = name,
    targetMinor = targetMinor,
    savedMinor = savedMinor,
    deadlineEpochDay = deadlineEpochDay,
    icon = icon,
    createdAtEpochMillis = createdAtEpochMillis,
    completed = completed,
)

internal fun SavingsGoal.toEntity(): SavingsGoalEntity = SavingsGoalEntity(
    id = id,
    name = name,
    targetMinor = targetMinor,
    savedMinor = savedMinor,
    deadlineEpochDay = deadlineEpochDay,
    icon = icon,
    createdAtEpochMillis = createdAtEpochMillis,
    completed = completed,
)

internal fun RecurringTransactionEntity.toDomain(): RecurringTransaction = RecurringTransaction(
    id = id,
    type = type.toTransactionType(),
    amountMinor = amountMinor,
    accountId = accountId,
    categoryId = categoryId,
    destinationAccountId = destinationAccountId,
    description = description,
    frequency = frequency.toRecurrenceFrequency(),
    nextOccurrenceEpochDay = nextOccurrenceEpochDay,
    endDateEpochDay = endDateEpochDay,
    active = active,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

internal fun RecurringTransaction.toEntity(): RecurringTransactionEntity = RecurringTransactionEntity(
    id = id,
    type = type.name,
    amountMinor = amountMinor,
    accountId = accountId,
    categoryId = categoryId,
    destinationAccountId = destinationAccountId,
    description = description,
    frequency = frequency.name,
    nextOccurrenceEpochDay = nextOccurrenceEpochDay,
    endDateEpochDay = endDateEpochDay,
    active = active,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

fun DailyTotalProjection.toDomain(): DailyTotal = DailyTotal(
    dateEpochDay = dateEpochDay,
    expenseMinor = expenseMinor,
    incomeMinor = incomeMinor,
)

fun SpendingByCategoryProjection.toDomain(): SpendingByCategory = SpendingByCategory(
    categoryId = categoryId,
    categoryName = categoryName,
    totalMinor = totalMinor,
    colorArgb = colorArgb,
)

private fun String.toAccountType(): AccountType =
    runCatching { AccountType.valueOf(this) }.getOrDefault(AccountType.OTHER)

private fun String.toCategoryKind(): CategoryKind =
    runCatching { CategoryKind.valueOf(this) }.getOrDefault(CategoryKind.EXPENSE)

private fun String.toTransactionType(): TransactionType =
    runCatching { TransactionType.valueOf(this) }.getOrDefault(TransactionType.EXPENSE)

private fun String.toBudgetPeriod(): BudgetPeriod =
    runCatching { BudgetPeriod.valueOf(this) }.getOrDefault(BudgetPeriod.MONTHLY)

private fun String.toRecurrenceFrequency(): RecurrenceFrequency =
    runCatching { RecurrenceFrequency.valueOf(this) }.getOrDefault(RecurrenceFrequency.MONTHLY)
