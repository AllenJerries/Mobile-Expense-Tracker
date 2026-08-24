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

/**
 * Validates and persists a new transaction. Validation rules:
 *  - amount must be strictly positive;
 *  - the referenced account must exist;
 *  - EXPENSE/INCOME transactions must reference an existing category.
 */
class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        if (transaction.amountMinor <= 0) {
            return Result.Failure(AppError.Validation(AMOUNT_MESSAGE))
        }
        if (accountRepository.getById(transaction.accountId) == null) {
            return Result.Failure(AppError.Validation(ACCOUNT_MESSAGE))
        }
        val categoryId = transaction.categoryId
        if (transaction.type != TransactionType.TRANSFER && categoryId == null) {
            return Result.Failure(AppError.Validation(CATEGORY_MESSAGE))
        }
        if (categoryId != null && categoryRepository.getById(categoryId) == null) {
            return Result.Failure(AppError.Validation(CATEGORY_MESSAGE))
        }
        return runCatchingResult {
            transactionRepository.add(transaction)
        }
    }

    companion object {
        const val AMOUNT_MESSAGE = "Amount must be greater than zero"
        const val ACCOUNT_MESSAGE = "Unknown account"
        const val CATEGORY_MESSAGE = "Select a valid category"
    }
}
