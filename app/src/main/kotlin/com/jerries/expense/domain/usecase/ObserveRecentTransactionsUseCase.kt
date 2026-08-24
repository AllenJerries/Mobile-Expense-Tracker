package com.jerries.expense.domain.usecase

import com.jerries.expense.domain.model.Transaction
import com.jerries.expense.domain.repository.TransactionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Streams the most recent transactions (newest first). */
class ObserveRecentTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(limit: Int = DEFAULT_LIMIT): Flow<List<Transaction>> =
        transactionRepository.observeRecent(limit)

    companion object {
        const val DEFAULT_LIMIT = 20
    }
}
