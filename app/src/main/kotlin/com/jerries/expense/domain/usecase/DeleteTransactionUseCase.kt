package com.jerries.expense.domain.usecase

import com.jerries.expense.core.common.AppError
import com.jerries.expense.core.common.Result
import com.jerries.expense.core.common.runCatchingResult
import com.jerries.expense.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(id: String, hardDelete: Boolean = false): Result<Unit> {
        val existing = transactionRepository.getById(id)
            ?: return Result.Failure(AppError.NotFound)
        return runCatchingResult {
            if (hardDelete) {
                transactionRepository.deleteById(id)
            } else {
                transactionRepository.softDeleteById(id)
            }
        }
    }
}
