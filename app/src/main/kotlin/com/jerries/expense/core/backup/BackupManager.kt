package com.jerries.expense.core.backup

import com.jerries.expense.data.local.dao.AccountDao
import com.jerries.expense.data.local.dao.BudgetDao
import com.jerries.expense.data.local.dao.CategoryDao
import com.jerries.expense.data.local.dao.RecurringTransactionDao
import com.jerries.expense.data.local.dao.SavingsGoalDao
import com.jerries.expense.data.local.dao.TransactionDao
import com.jerries.expense.data.local.entity.AccountEntity
import com.jerries.expense.data.local.entity.BudgetEntity
import com.jerries.expense.data.local.entity.CategoryEntity
import com.jerries.expense.data.local.entity.RecurringTransactionEntity
import com.jerries.expense.data.local.entity.SavingsGoalEntity
import com.jerries.expense.data.local.entity.TransactionEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

/**
 * Handles bulk export/import operations directly against Room DAOs.
 * The caller is responsible for running on a background dispatcher.
 */
@Singleton
class BackupManager @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val budgetDao: BudgetDao,
    private val savingsGoalDao: SavingsGoalDao,
    private val recurringTransactionDao: RecurringTransactionDao,
    private val codec: BackupCodec,
) {

    suspend fun buildPayload(): BackupPayload {
        val accounts = accountDao.observeAll().first()
        val categories = categoryDao.observeAll().first()
        val transactions = transactionDao.observeAll().first()
        val budgets = budgetDao.observeAll().first()
        val goals = savingsGoalDao.observeAll().first()
        val recurring = recurringTransactionDao.observeAll().first()

        return BackupPayload(
            metadata = BackupMetadata(
                exportedAtEpochMillis = System.currentTimeMillis(),
                transactionCount = transactions.size,
                accountCount = accounts.size,
                categoryCount = categories.size,
                budgetCount = budgets.size,
                goalCount = goals.size,
                recurringCount = recurring.size,
            ),
            accounts = accounts.map { it.toBackup() },
            categories = categories.map { it.toBackup() },
            transactions = transactions.map { it.toBackup() },
            budgets = budgets.map { it.toBackup() },
            goals = goals.map { it.toBackup() },
            recurringTransactions = recurring.map { it.toBackup() },
        )
    }

    suspend fun restoreFromPayload(payload: BackupPayload) {
        transactionDao.deleteAll()
        recurringTransactionDao.deleteAll()
        budgetDao.deleteAll()
        savingsGoalDao.deleteAll()
        categoryDao.deleteAll()
        accountDao.deleteAll()

        payload.accounts.forEach { accountDao.upsert(it.toEntity()) }
        payload.categories.forEach { categoryDao.upsert(it.toEntity()) }
        payload.transactions.forEach { transactionDao.upsert(it.toEntity()) }
        payload.budgets.forEach { budgetDao.upsert(it.toEntity()) }
        payload.goals.forEach { savingsGoalDao.upsert(it.toEntity()) }
        payload.recurringTransactions.forEach { recurringTransactionDao.upsert(it.toEntity()) }
    }

    fun encode(payload: BackupPayload): String = codec.encode(payload)

    fun decode(raw: String): Result<BackupPayload> = codec.decode(raw)
}

// ── Backup ↔ Entity mappers ──────────────────────────────────────────────

private fun AccountEntity.toBackup(): BackupAccount = BackupAccount(
    id = id,
    name = name,
    type = type,
    initialBalanceMinor = initialBalanceMinor,
    currencyCode = currencyCode,
    colorArgb = colorArgb,
    archived = archived,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

private fun BackupAccount.toEntity(): AccountEntity = AccountEntity(
    id = id,
    name = name,
    type = type,
    initialBalanceMinor = initialBalanceMinor,
    currencyCode = currencyCode,
    colorArgb = colorArgb,
    archived = archived,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

private fun CategoryEntity.toBackup(): BackupCategory = BackupCategory(
    id = id,
    name = name,
    kind = kind,
    iconKey = iconKey,
    colorArgb = colorArgb,
    isDefault = isDefault,
    isArchived = isArchived,
)

private fun BackupCategory.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    kind = kind,
    iconKey = iconKey,
    colorArgb = colorArgb,
    isDefault = isDefault,
    isArchived = isArchived,
)

private fun TransactionEntity.toBackup(): BackupTransaction = BackupTransaction(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    amountMinor = amountMinor,
    type = type,
    dateEpochDay = dateEpochDay,
    title = title,
    note = note,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
    paymentMethod = paymentMethod,
    destinationAccountId = destinationAccountId,
    recurringTransactionId = recurringTransactionId,
    isDeleted = isDeleted,
)

private fun BackupTransaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    amountMinor = amountMinor,
    type = type,
    dateEpochDay = dateEpochDay,
    title = title,
    note = note,
    createdAtEpochMillis = createdAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
    paymentMethod = paymentMethod,
    destinationAccountId = destinationAccountId,
    recurringTransactionId = recurringTransactionId,
    attachmentUri = null,
    isDeleted = isDeleted,
)

private fun BudgetEntity.toBackup(): BackupBudget = BackupBudget(
    id = id,
    categoryId = categoryId,
    accountId = accountId,
    limitMinor = limitMinor,
    period = period,
    startEpochDay = startEpochDay,
    endEpochDay = endEpochDay,
    alertThreshold = alertThreshold,
    createdAtEpochMillis = createdAtEpochMillis,
)

private fun BackupBudget.toEntity(): BudgetEntity = BudgetEntity(
    id = id,
    categoryId = categoryId,
    accountId = accountId,
    limitMinor = limitMinor,
    period = period,
    startEpochDay = startEpochDay,
    endEpochDay = endEpochDay,
    alertThreshold = alertThreshold,
    createdAtEpochMillis = createdAtEpochMillis,
)

private fun SavingsGoalEntity.toBackup(): BackupGoal = BackupGoal(
    id = id,
    name = name,
    targetMinor = targetMinor,
    savedMinor = savedMinor,
    deadlineEpochDay = deadlineEpochDay,
    icon = icon,
    createdAtEpochMillis = createdAtEpochMillis,
    completed = completed,
)

private fun BackupGoal.toEntity(): SavingsGoalEntity = SavingsGoalEntity(
    id = id,
    name = name,
    targetMinor = targetMinor,
    savedMinor = savedMinor,
    deadlineEpochDay = deadlineEpochDay,
    icon = icon,
    createdAtEpochMillis = createdAtEpochMillis,
    completed = completed,
)

private fun RecurringTransactionEntity.toBackup(): BackupRecurringTransaction =
    BackupRecurringTransaction(
        id = id,
        type = type,
        amountMinor = amountMinor,
        accountId = accountId,
        categoryId = categoryId,
        destinationAccountId = destinationAccountId,
        description = description,
        frequency = frequency,
        nextOccurrenceEpochDay = nextOccurrenceEpochDay,
        endDateEpochDay = endDateEpochDay,
        active = active,
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
    )

private fun BackupRecurringTransaction.toEntity(): RecurringTransactionEntity =
    RecurringTransactionEntity(
        id = id,
        type = type,
        amountMinor = amountMinor,
        accountId = accountId,
        categoryId = categoryId,
        destinationAccountId = destinationAccountId,
        description = description,
        frequency = frequency,
        nextOccurrenceEpochDay = nextOccurrenceEpochDay,
        endDateEpochDay = endDateEpochDay,
        active = active,
        createdAtEpochMillis = createdAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
    )
