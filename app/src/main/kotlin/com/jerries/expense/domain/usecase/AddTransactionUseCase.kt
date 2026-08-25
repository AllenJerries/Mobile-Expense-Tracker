package com.jerries.expense.domain.usecase

import com.jerries.expense.core.common.AppError
import com.jerries.expense.core.common.Result
import com.jerries.expense.core.common.runCatchingResult
import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.model.TransactionType
import com.jerries.expense.domain.repository.AccountRepository
import com.jerries.expense.domain.repository.CategoryRepository
import com.jerries.expense.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        if (transaction.amountMinor <= 0) {
            return Result.Failure(AppError.Validation(AMOUNT_MESSAGE))
        }
        if (transaction.dateEpochDay <= 0) {
            return Result.Failure(AppError.Validation(DATE_MESSAGE))
        }
        val account = accountRepository.getById(transaction.accountId)
        if (account == null) {
            return Result.Failure(AppError.Validation(ACCOUNT_MESSAGE))
        }
        if (account.archived) {
            return Result.Failure(AppError.Validation(ARCHIVED_ACCOUNT_MESSAGE))
        }
        if (transaction.type == TransactionType.TRANSFER) {
            val destAccountId = transaction.destinationAccountId
            if (destAccountId == null || destAccountId == transaction.accountId) {
                return Result.Failure(AppError.Validation(TRANSFER_DEST_MESSAGE))
            }
            val destAccount = accountRepository.getById(destAccountId)
            if (destAccount == null || destAccount.archived) {
                return Result.Failure(AppError.Validation(TRANSFER_DEST_INVALID_MESSAGE))
            }
        } else {
            val categoryId = transaction.categoryId
            if (categoryId == null) {
                return Result.Failure(AppError.Validation(CATEGORY_MESSAGE))
            }
            val category = categoryRepository.getById(categoryId)
            if (category == null) {
                return Result.Failure(AppError.Validation(CATEGORY_MESSAGE))
            }
            if (category.isArchived) {
                return Result.Failure(AppError.Validation(ARCHIVED_CATEGORY_MESSAGE))
            }
        }
        return runCatchingResult {
            transactionRepository.add(transaction)
        }
    }

    companion object {
        const val AMOUNT_MESSAGE = "Amount must be greater than zero"
        const val DATE_MESSAGE = "Invalid date"
        const val ACCOUNT_MESSAGE = "Unknown account"
        const val ARCHIVED_ACCOUNT_MESSAGE = "Cannot use an archived account"
        const val CATEGORY_MESSAGE = "Select a valid category"
        const val ARCHIVED_CATEGORY_MESSAGE = "Cannot use an archived category"
        const val TRANSFER_DEST_MESSAGE = "Transfer requires a different destination account"
        const val TRANSFER_DEST_INVALID_MESSAGE = "Invalid destination account"
    }
}
